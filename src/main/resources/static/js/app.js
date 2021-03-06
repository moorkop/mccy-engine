angular.module('MccyApp', [
    'Mccy.controllers',
    'Mccy.routes',
    'Mccy.services',
    'Mccy.directives',
    'Mccy.mods',
    'Mccy.constants',
    'Mccy.filters',
    'ngAnimate',
    'ui.bootstrap',
    'ui.bootstrap.tpls',
    'ngTagsInput',
    'AngularStompDK'
])
    .config(function(ngstompProvider){
        ngstompProvider
            .url('/stomp')
            .class(SockJS)
    })

    .controller('MainCtrl', function ($scope, $timeout, $location, $log, $window,
                                      $uibModal,
                                      Containers, MccyApi, Versions, Users,
                                      Alerts, MccyViews,
                                      cToasterOptions, cBaseVersions) {
        $scope.settings = {};

        $scope.versions = cBaseVersions;

        $scope.logout = function() {
            Users.logout();
            $window.location.reload();
        };

        $scope.toasterOptions = cToasterOptions;

        // NOTE: first view is assumed to be the default
        $scope.views = MccyViews;

        $scope.$on('$routeChangeSuccess', function(evt, current){
            $scope.currentView = current.originalPath;
            $log.debug('route change', current);
        });

        $scope.isCurrentView = function(v) {
            return $scope.currentView == v.view;
        };

        $scope.goto = function(newView) {
            $location.url(newView.view);
        };

        MccyApi.settings(function(response){
            // Convert the dot-delimited keys of the response into native
            // object paths of $scope.settings
            _.forEach(response, function(value, key){
                // ...not Angular's fields
                if (!_.startsWith(key, '$')) {
                    // ...and normalize the object keys to camel case so we don't have to
                    // quote the dash-separated identifiers from the backend

                    var path = key.split('.');
                    _.set($scope.settings, _.map(path, _.camelCase), value);
                }
            });

            $log.debug('Loaded UI settings', $scope.settings);
        });

        $scope.$on('closeNewContainerArea', function () {
            $scope.goto($scope.views[0]);
        });

    })

    .factory('sessionTimeoutInterceptor', function($log, $window) {
        return {
            response: function(response){
                if (response.headers('x-login') === 'true') {
                    $log.debug('Intercepted login redirect', response);
                    // TODO prompt the user
                    $window.location.reload();
                }
                return response;
            }
        }
    })

    .config(function($httpProvider){
        $httpProvider.interceptors.push('sessionTimeoutInterceptor');
    })

;