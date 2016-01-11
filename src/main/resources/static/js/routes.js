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
            .when('/upload-mod', {
                templateUrl: 'views/upload-mod.html',
                controller: 'UploadModCtrl'
            })
            .when('/manage-mods', {
                templateUrl: 'views/manage-mods.html',
                controller: 'ManageModsCtrl'
            })
            .otherwise('/view')
    })

    .constant('MccyViews', [
        {
            view: '/view',
            label: 'Current Containers'
	    icon: 'icon fa fa-heartbeat'
	    show: true
        },
        {
            view: '/new-server',
            label: 'New Container'
	    icon: 'icon fa fa-magic'
	    show: true
        },
        {
            view: '/upload-mod',
            label: 'Upload Mods'
	    icon: 'icon fa fa-upload'
	    show: true
        },
        {
            view: '/manage-mods',
            label: 'Manage Mods'
	    icon: 'icon fa fa-flask'
	    show: true
        }
    ])
;
