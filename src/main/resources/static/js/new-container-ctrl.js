angular.module('Mccy.NewContainerCtrl', [
        'Mccy.services',
        'ngFileUpload'
    ])
    .run(function(editableOptions) {
        editableOptions.theme = 'bs3';
    })
    .controller('NewContainerCtrl', function ($scope, $log, Upload,
                                              Containers, Alerts, Versions, Mods, ModPacks,
                                              cServerTypes, cModdedTypes) {

        // Start with master list...
        $scope.applicableVersions = $scope.versions;

        $scope.types = _.map(cServerTypes, function(value, key){
            return {
                label: value,
                value: key
            }
        });

        Versions.query({type:'VANILLA'}, function(response){
            $scope.versions = $scope.versions.concat(response.map(function(v){
                return {
                    value: v,
                    label: v
                }
            }))
        });

        reset();

        $scope.$watch('type', function(newValue){
            if (_.includes(cModdedTypes, newValue)) {
                Versions.query({type:newValue}, function(response){
                    $scope.applicableVersions = _.map(response, function(v) {
                        return {
                            value: v,
                            label: v
                        }
                    });
                    $scope.version = response[0];
                });
                $scope.chooseMods = true;
            }
            else {
                $scope.applicableVersions = $scope.versions;
                $scope.chooseMods = false;
            }
        });

        //$scope.$watchGroup(['type', 'version'], function (newValues) {
        //    if (newValues[0] == 'FORGE') {
        //        $scope.applicableMods = Mods.query({mcversion:newValues[1]});
        //    }
        //    else {
        //        $scope.applicableMods = null;
        //    }
        //});

        $scope.submitNewContainer = function () {
            $log.debug('submitting', this);
            $scope.creating = true;

            var request = {
                ackEula: this.ackEula,
                name: this.name,
                port: this.choosePort ? this.port : 0,
                type: this.type,
                version: this.version
            };

            if (this.enableOperators) {
                request.ops = this.ops.map(extractTagText);
            }
            if (this.enableWhitelist) {
                request.whitelist = this.whitelist.map(extractTagText);
            }
            if (this.enableIcon) {
                request.icon = this.iconUrl;
            }
            if (this.enableWorld) {
                request.world = this.worldUrl;
            }
            if (this.enablePublic) {
                request.visibleToPublic = this.enablePublic;
            }
            if (this.chooseMods && !_.isEmpty(this.selectedMods)) {
                ModPacks.save(_.map(this.selectedMods, function(m){
                    return {
                        type: m.type,
                        id: m.id
                    }
                }), function(response){
                    request.modpack = response.value;
                    proceedAfterMods(request);
                })
            }
            else {
                proceedAfterMods(request);
            }
        };

        function proceedAfterMods(request) {
            if ($scope.worldFile) {
                Upload.upload({
                    url: '/api/uploads/worlds',
                    data: {file: $scope.worldFile}
                }).then(function (response) {
                    // success
                    Alerts.info('World Uploaded', 'World file was uploaded successfully');

                    request.world = response.data.value;
                    $log.debug("Upload available at", request.world);
                    proceedAfterWorld(request);
                }, function (resp) {
                    var msg = resp.statusText;

                    if (!msg) {
                        msg = 'Failed to upload world file. Make sure it is smaller than ' +
                            $scope.settings.mccy.maxUploadSize;
                    }

                    $log.warn(resp);
                    Alerts.error(msg);
                });
            }
            else {
                proceedAfterWorld(request);
            }
        }

        function proceedAfterWorld(request) {
            Containers.save(request, handleSuccess, handleRequestError);
        }

        $scope.suggestMods = function(input) {
            return Mods.query({
                id: '_suggest',
                mcversion: $scope.version,
                type: $scope.type,
                input: input}).$promise;
        };

        $scope.cancelNewContainer = function () {
            close();
        };

        function extractTagText(tag) {
            return tag.text;
        }

        function handleSuccess() {
            Alerts.success('The server container was successfully created', true);

            close();
        }

        function handleRequestError(httpResponse) {
            Alerts.error(httpResponse.data.message, true);
            $scope.creating = false;
        }

        function close() {
            reset();
            $scope.$emit('closeNewContainerArea');
        }

        function reset() {
            $scope.name = undefined;
            $scope.choosePort = false;
            $scope.port = 25565;
            $scope.type = $scope.types[0].value;
            $scope.version = 'LATEST';
            $scope.enableOperators = false;
            $scope.ops = [];
            $scope.enableWhitelist = false;
            $scope.whitelist = [];
            $scope.enableIcon = false;
            $scope.iconUrl = undefined;
            $scope.enableWorld = false;
            $scope.enablePublic = false;
            $scope.worldUrl = undefined;
            $scope.ackEula = false;
            $scope.worldUploaded = false;
            $scope.worldFile = undefined;
        }

    })

;