angular.module('Mccy.ManageModsCtrl', [])

    .controller('ManageModsCtrl', function ($scope, Mods) {

        $scope.$on('reload', function () {
            reload();
        });

        reload();


        function reload() {
            $scope.registeredMods = Mods.query();
        }
    })
;