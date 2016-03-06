angular.module('Mccy.mods',[
        'xeditable'
    ])

    .directive('mccyModPanel', function(){

        function controller($scope, $rootScope, $filter, Alerts, Mods, Versions) {
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

            $scope.compatibleServerTypes = _.map($scope.mod.serverTypes, function(envType){
                return $filter('serverTypeLabel')(envType);
            }).join(', ');

            $scope.update = function() {
                Versions.query({type:$scope.mod.serverTypes[0]}, function(response){
                    $scope.applicableVersions = _.map(response, function(v) {
                        return {
                            value: v,
                            label: v
                        }
                    });
                });
            }
        }

        return {
            templateUrl: 'ng-bits/mod-panel.html',
            restrict: 'A',
            scope: {
                mod: '=mccyModPanel',
                showDelete: '=mccyModPanelShowDelete',
            },
            controller: controller
        }
    })

;