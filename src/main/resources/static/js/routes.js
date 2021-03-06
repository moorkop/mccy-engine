angular.module('Mccy.routes', [
    'ngRoute'
])

.config(function ($routeProvider) {
    $routeProvider
    .when('/view', {
        templateUrl: 'views/view-containers.html',
        controller: 'ViewContainersCtrl'
    })
    .when('/new-server', {
        templateUrl: 'views/create-container.html',
        controller: 'NewContainerCtrl'
    })
    .when('/manage-mods', {
        templateUrl: 'views/manage-mods.html',
        controller: 'ManageModsCtrl'
    })
    .when('/manage-assets/:assetCategory', {
        templateUrl: 'views/manage-assets.html',
        controller: 'ManageAssetsCtrl'
    })
    .otherwise('/view')
})

.constant('MccyViews', [
    {
        view: '/view',
        settings: {
            label: 'Current Containers',
            icon: 'icon fa fa-heartbeat'
        }
    },
    {
        view: '/new-server',
        settings: {
            label: 'Create Container',
            icon: 'icon fa fa-magic'
        }
    },
    {
        view: '/manage-mods',
        settings: {
            label: 'Manage Mods',
            icon: 'icon fa fa-flask'
        }
    },
    {
        view: '/manage-assets/WORLD',
        settings: {
            label: 'Manage Worlds',
            icon: 'icon fa fa-globe'
        }
    }
])
;
