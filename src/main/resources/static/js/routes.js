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
            .otherwise('/view')
    })
;