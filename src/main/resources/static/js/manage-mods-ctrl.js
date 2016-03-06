angular.module('Mccy.ManageMods', [
        'Mccy.services',
        'ngFileUpload'
    ])

    .controller('ManageModsCtrl', function ($scope, Mods) {

        $scope.$on('reload', function () {
            reload();
        });

        reload();


        function reload() {
            $scope.registeredMods = Mods.query();
        }
    })

    .controller('UploadModCtrl', function ($scope, $log, $timeout, Upload, Alerts, cTimeouts) {

        $scope.registeredMods = [];

        $scope.allowedPatterns = '.jar,.url';

        $scope.validate = function(file) {
            $log.debug('Request to validate', file);
            return true;
        };

        $scope.uploadModFile = function (files) {
            Upload.upload({
                url: '/api/uploads/mods',
                data: {
                    files: files
                },
                // Spring MVC multi part is expecting each multipart named as 'files'
                arrayKey: ''
            }).then(
                function (successResponse) {
                    $log.debug('Mod upload response', successResponse);

                    //$scope.hideUploadArea = true;
                    var newMods = successResponse.data.mods;
                    var combined = $scope.registeredMods.concat(newMods);
                    $scope.registeredMods = _.uniq(combined, 'id');
                    $log.debug('Current mod list after de-duping', $scope.registeredMods);

                    if (successResponse.data.failed) {
                        _.forEach( successResponse.data.failed, function(f) {
                            $log.warn(f);
                            Alerts.error(f.filename + ' failed due to ' + f.reason);
                        });
                    }

                    $timeout(resetProgress, cTimeouts.resetProgress);

                    startVersionCheck(newMods);
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

        function startVersionCheck(registeredMods) {
            _.forEach(registeredMods, function(registeredMod){
                registeredMod.otherVersionsOfThis = []; //TODO
            });
        }
    })
;