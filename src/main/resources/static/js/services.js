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
            },
            getStatus: {
                url: '/api/containers/:id/_status',
                method: 'GET'
            }
        });
    })

    .service('PublicContainers', function($resource){
        return $resource('/api/containers/_public/:id/:action', {}, {
            getStatus: {
                params: {
                    action: '_status'
                },
                method: 'GET'
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
        return $resource('/api/mods/:id', {id:"@id"});
    })

    .service('Assets', function($resource){
        return $resource('/api/assets/:category/:id/:action',
            {
                category:"@category",
                id:"@id"
            },
            {
                suggest: {
                    params: {
                        action: '_suggest'
                    },
                    isArray: true
                }
            });
    })

    .service('ModPacks', function($resource){
        return $resource('/api/modpacks');
    })

    .service('Users', function($resource){
        return $resource('/', {}, {
            logout: {
                url: '/logout',
                method: 'POST'
            }
        });
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