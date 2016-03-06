angular.module('Mccy.ManageAssets', [
        'Mccy.services',
        'ngFileUpload'
    ])

    .controller('ManageAssetsCtrl', function ($scope, $routeParams, Assets, cAssetSpecs) {
        var assetCategory = $routeParams.assetCategory;

        $scope.$on('reload', function () {
            reload();
        });

        $scope.assetSpec = cAssetSpecs[assetCategory];

        reload();

        function reload() {
            $scope.registeredAssets = Assets.query({category: assetCategory});
        }
    })

    .controller('UploadAssetsCtrl', function ($scope, $log, $timeout, Upload, Alerts, cTimeouts, $routeParams) {
        var assetCategory = $routeParams.assetCategory;

        $scope.registeredAssets = [];

        $scope.allowedPatterns = '.zip';

        $scope.validate = function(file) {
            $log.debug('Request to validate', file);
            return true;
        };

        $scope.uploadFile = function (file) {
            Upload.upload({
                url: '/a',
                data: {
                    file: file,
                    category: assetCategory
                },
                // Spring MVC multi part is expecting each multipart named as 'files'
                arrayKey: ''
            }).then(
                function (successResponse) {
                    $log.debug('Asset upload response', successResponse);

                    //$scope.hideUploadArea = true;
                    var newAsset = successResponse.data;
                    var combined = $scope.registeredAssets.concat(newAsset);
                    $scope.registeredAssets = _.uniq(combined, 'id');
                    $log.debug('Current mod list after de-duping', $scope.registeredAssets);

                    if (successResponse.data.failed) {
                        _.forEach( successResponse.data.failed, function(f) {
                            $log.warn(f);
                            Alerts.error(f.filename + ' failed due to ' + f.reason);
                        });
                    }

                    $timeout(resetProgress, cTimeouts.resetProgress);

                },
                function (failureResponse) {
                    $log.warn('Upload failed', failureResponse);
                    Alerts.error('Failed to upload mod file', false);
                },
                function (evt) {
                    $log.debug('Progressing', evt);
                    $scope.progress = parseInt(100.0 * evt.loaded / evt.total);
                }
            );

            $scope.showProgress = true;
            $scope.progress = 0;
        };

        function resetProgress() {
            $scope.showProgress = false;
            $scope.progress = 0;
        }
    })
;