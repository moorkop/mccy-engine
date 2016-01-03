angular.module('Mccy.services',[
        'toaster',
        'ngResource'
    ])

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

    .service('MccyApi', function($resource){
        return $resource('/api/:category', {}, {
            appInfo: {
                params: { category: 'info' }
            },
            settings: {
                params: { category: 'settings' }
            }
        });
    })

    .service('Versions', function($resource){
        return $resource('/api/versions/:type');
    })

    .service('Mods', function($resource){
        return $resource('/api/mods/:id');
    })

    .service('ModPacks', function($resource){
        return $resource('/api/modpacks');
    })

    .service('Alerts', function($rootScope, toaster){
        function popAndReload(level, title, msg, reload, timeout) {
            toaster.pop(level, title, msg, timeout);
            if (reload) {
                $rootScope.$broadcast('reload');
            }
        }

        return {
            success: function (msg, reload) {
                reload = reload || false;
                popAndReload('success', 'Success', msg, reload);
            },

            error: function(msg, reload) {
                reload = reload || false;
                popAndReload('error', 'Failed', msg, reload);
            },

            info: function (title, msg) {
                popAndReload('info', title, msg, false, 2000);
            },

            handleRequestError: function(httpResponse) {
                popAndReload('error', 'Request Failed', httpResponse.message ||
                    (httpResponse.data && httpResponse.data.message));
            }
        }
    })

    .filter('serverTypeLabel', function(cServerTypes) {
        return function(input) {
            if (_.isUndefined(input)) {
                return input;
            }
            return _.get(cServerTypes, input, _.startCase(input.toLowerCase()));
        };
    })
;