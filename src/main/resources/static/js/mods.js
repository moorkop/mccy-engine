angular.module('Mccy.mods',[
    'xeditable'
])

    .directive('mccyModPanel', function(){

        function controller($scope, $rootScope, $filter, Alerts, Mods) {
            $scope.delete = function() {
                Mods.delete({id:$scope.mod.jarChecksum}, {}, function() {
                    $rootScope.$broadcast('reload');
                },
                function(errResp){
                    Alerts.error(errResp.statusText, false);
                });
            };

            $scope.markDirty = function() {
                $scope.dirty = true;
            };

            $scope.save = function() {
                $scope.mod.$save(function(){
                    $scope.dirty = false;
                });
            };

            $scope.compatibleServerTypes = $scope.mod.serverTypes.map(function(envType){
                return $filter('serverTypeLabel')(envType);
            }).join(', ');
        }

        return {
            templateUrl: 'ng-bits/mod-panel.html',
            restrict: 'A',
            scope: {
                mod: '=mccyModPanel',
                showDelete: '=mccyModPanelShowDelete'
            },
            controller: controller
        }
    })

;