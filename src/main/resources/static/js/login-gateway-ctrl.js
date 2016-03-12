angular.module('Mccy.LoginLandingCtrl',[])

    .controller('LoginCtrl', function($scope, PublicContainers, Alerts) {

        reload();

        // Need to refactor this and the same code in ViewContainersCtrl
        function fetchContainerDetails(containers) {
            _.each(containers, function (container) {
                PublicContainers.get({id:container.id}, function(details){
                    _.assign(container, details.summary);
                    container.info = details.info;
                }, Alerts.handleRequestError);

                PublicContainers.getStatus({id:container.id}, function(status){
                    container.status = _.assign({
                        _resolved: true,
                        iconSrc: 'data:image/png;base64,' + status.icon
                    }, status);
                }, Alerts.handleRequestError);
            });
        }

        function reload() {
            $scope.containers = PublicContainers.query(function (response) {
                fetchContainerDetails(response);
            }, Alerts.handleRequestError);
        }

        //TODO
    })
;