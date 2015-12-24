angular.module('MccyApp', [
    'Mccy.NewContainerCtrl',
    'ngResource',
    'ngAnimate',
    'ui.bootstrap',
    'template/modal/backdrop.html',
    'template/modal/window.html',
    'ngTagsInput',
    'toaster'
])

    .controller('MainCtrl', function ($scope, $timeout, $uibModal, toaster, Containers, AppInfo) {
        $scope.showNewContainerArea = false;

        $scope.types = [
            {
                value: 'VANILLA',
                label: 'Regular'
            },
            {
                value: 'FORGE',
                label: 'Forge'
            }
        ];

        $scope.versions = [
            {
                value: 'LATEST',
                label: 'Latest Stable'
            },
            {
                value: 'SNAPSHOT',
                label: 'Snapshot'
            }
        ];

        $scope.toasterOptions = {
            'time-out': {
                'toast-error': 0,
                'toast-success': 3000
            },
            'close-button': true
        };

        $scope.appInfo = AppInfo.get();

        load();

        $scope.createNewContainer = function() {
            $scope.showNewContainerArea = true;
        };

        $scope.$on('closeNewContainerArea', function () {
            $scope.showNewContainerArea = false;
        });

        $scope.$on('reloadContainers', function() {
            delayedReload();
        });

        $scope.refresh = function() {
            load();
        };

        $scope.isRunning = function(container) {
            if (container.status && container.status.indexOf('Exited') == 0) {
                return false;
            }
            if (container.running === true) {
                return true;
            }
            // TODO use container details to find out for sure
            return true;
        };

        $scope.stop = function(container) {
            Containers.stop({id: container.id}, {}, handleSuccess, handleRequestError);
        };

        $scope.start = function(container) {
            Containers.start({id: container.id}, {}, handleSuccess, handleRequestError);
        };

        $scope.remove = function(container) {
            $uibModal.open({
                templateUrl: 'bits/confirm-container-remove.html'
            }).result.then(function(answer){
                if (answer === true) {
                    Containers.remove({id: container.id}, {}, handleSuccess, handleRequestError);
                }
            });
        };

        function handleSuccess(value) {
            toaster.pop('success', 'Success', 'The request was submitted succesfully');
            delayedReload();
        }

        function handleRequestError(httpResponse) {
            toaster.pop('error', 'Request Failed', httpResponse.data.message);
            delayedReload();
        }

        function delayedReload() {
            $timeout(load, 500);
        }

        function load() {
            $scope.containers = Containers.query(function(){}, handleRequestError);
        }
    })


    .service('Containers', function($resource){
        return $resource('/api/containers/:id', {}, {
            stop: {
                url: '/api/containers/:id/_stop',
                method: 'POST'
            },
            start: {
                url: '/api/containers/:id/_start',
                method: 'POST'
            }
        });
    })

    .service('AppInfo', function($resource){
        return $resource('/api/info');
    })
;