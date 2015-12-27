angular.module('MccyApp', [
    'Mccy.NewContainerCtrl',
    'Mccy.ViewContainersCtrl',
    'Mccy.routes',
    'Mccy.services',
    'ngAnimate',
    'ui.bootstrap',
    'template/modal/backdrop.html',
    'template/modal/window.html',
    'ngTagsInput'
])

    .controller('MainCtrl', function ($scope, $timeout, $location, $log,
                                      $uibModal, Containers, AppInfo, Alerts) {
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

        // NOTE: first view is assumed to be the default
        $scope.views = [
            {
                view: '/view',
                label: 'View Current Servers'
            },
            {
                view: '/new-server',
                label: 'Create New Server'
            }
        ];

        $scope.$on('$routeChangeSuccess', function(evt, current, previous){
            $scope.currentView = current.originalPath;
            $log.debug('route change', current);
        });

        $scope.isCurrentView = function(v) {
            return $scope.currentView == v.view;
        };

        $scope.goto = function(newView) {
            $location.url(newView.view);
        };

        $scope.appInfo = AppInfo.get();

        $scope.$on('closeNewContainerArea', function () {
            $scope.goto($scope.views[0]);
        });

    })


;