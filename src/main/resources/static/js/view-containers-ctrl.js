angular.module('Mccy.ViewContainersCtrl', [
        'Mccy.services',
        'ui.bootstrap',
        'template/modal/backdrop.html',
        'template/modal/window.html',
    ])

    .controller('ViewContainersCtrl', function ($scope, $uibModal, Containers, Alerts) {

        function reload() {
            $scope.containers = Containers.query(function () {
            }, Alerts.handleRequestError);
        }

        $scope.reload = function () {
            reload();
        };

        $scope.$on('reload', function () {
            reload();
        });

        reload();

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
            Containers.stop({id: container.id}, {}, handleSuccess, handleRequestError);
        };

        $scope.start = function (container) {
            Containers.start({id: container.id}, {}, handleSuccess, handleRequestError);
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

    })
;