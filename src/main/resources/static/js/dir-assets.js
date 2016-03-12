angular.module('Mccy.directive.assets',[
        'xeditable'
    ])

    .directive('mccyAssetPanel', function(){

        function controller($scope, $rootScope, $filter, Alerts, Versions, Assets) {
            $scope.delete = function() {
                $scope.asset.$delete(function() {
                    $rootScope.$broadcast('reload');
                }, function(errResp){
                    Alerts.error(errResp.statusText, false);
                });
            };

            $scope.markDirty = function() {
                $scope.dirty = true;
            };

            function undirty() {
                $scope.dirty = false;
            }

            $scope.save = function() {
                if (_.isFunction($scope.asset.$save)) {
                    $scope.asset.$save(undirty);
                }
                else {
                    Assets.save($scope.asset, undirty);
                }
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