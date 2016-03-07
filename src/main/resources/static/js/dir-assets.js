angular.module('Mccy.directive.assets',[
        'xeditable'
    ])

    .directive('mccyAssetPanel', function(){

        function controller($scope, $rootScope, $filter, Alerts, Mods, Versions) {
            $scope.delete = function() {
                Mods.delete({id:$scope.asset.jarChecksum}, {}, function() {
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
                $scope.asset.$save(function(){
                    $scope.dirty = false;
                });
            };

            $scope.compatibleServerTypes = _.map($scope.asset.serverTypes, function(envType){
                return $filter('serverTypeLabel')(envType);
            }).join(', ');

            $scope.update = function() {
                Versions.query({type:$scope.asset.serverTypes[0]}, function(response){
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
            templateUrl: 'ng-bits/dir-asset-panel.html',
            restrict: 'A',
            scope: {
                asset: '=mccyAssetPanel',
                showDelete: '=mccyAssetPanelShowDelete',
            },
            controller: controller
        }
    })

;