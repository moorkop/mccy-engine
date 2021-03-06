angular.module('Mccy.ViewContainersCtrl', [
        'Mccy.services',
        'ui.bootstrap',
        'angular-clipboard'
    ])

    .controller('ViewContainersCtrl', function ($scope, $timeout, $uibModal, Containers, Alerts) {

        $scope.reload = function () {
            reload();
        };

        $scope.$on('reload', function () {
            reload();
        });

        reload();

        $scope.hasConnectInfo = function(c) {
            return !_.isUndefined(c.hostIp);
        };

        $scope.isRunning = function (container) {
            if (container.status && container.status.indexOf('Exited') == 0) {
                return false;
            }
            if (container.running === true) {
                return true;
            }
            // TODO use container details to find out for sure
            return true;
        };

        $scope.stop = function (container) {
            Containers.stop({id: container.id}, {}, function(){
                Alerts.success("Container stopped");
                reload();
            }, Alerts.handleRequestError);
        };

        $scope.start = function (container) {
            Containers.start({id: container.id}, {}, function(){
                Alerts.success("Container started");
                reload();
            }, Alerts.handleRequestError);
        };

        $scope.remove = function (container) {
            $uibModal.open({
                templateUrl: 'ng-bits/confirm-container-remove.html'
            }).result.then(function (answer) {
                if (answer === true) {
                    Containers.remove({id: container.id}, {}, function() {
                        Alerts.success('Server was successfully removed', true);
                    }, Alerts.handleRequestError);
                }
            });
        };

        function fetchContainerDetails(containers) {
            _.each(containers, function (container) {
                Containers.get({id:container.id}, function(details){
                    _.assign(container, details.summary);
                    container.info = details.info;
                }, Alerts.handleRequestError);
            });
        }

        function reload() {
            $scope.containers = Containers.query(function (response) {
                fetchContainerDetails(response);
            }, Alerts.handleRequestError);
        }

    })
;