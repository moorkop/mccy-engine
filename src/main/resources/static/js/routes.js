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
		view: '/upload-mod',
		settings: {
			label: 'Upload Mods',
			icon: 'icon fa fa-upload'
		}
	},
	{
		view: '/manage-mods',
		settings: {
			label: 'Manage Mods',
			icon: 'icon fa fa-flask'
		}
	}
])
;
