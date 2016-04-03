angular.module('Mccy.NewContainerCtrl', [
        'Mccy.services',
        'ngFileUpload',
        'ui.select',
        'ngSanitize'
    ])
    .run(function(editableOptions) {
        editableOptions.theme = 'bs3';
    })
    .controller('NewContainerCtrl', function ($scope, $log, Upload, ngstomp,
                                              Containers, Alerts, Versions, Mods, ModPacks, Assets,
                                              cServerTypes, cModdedTypes) {

        // Start with master list...
        $scope.applicableVersions = $scope.versions;

        // An explicit location to place some model bindings, such as the ui-select
        $scope.choices = {};

        $scope.types = _.map(cServerTypes, function(value, key){
            return {
                label: value,
                value: key
            }
        });

        Versions.query({type:'VANILLA'}, function(response){
            $scope.applicableVersions = $scope.versions.concat(response.map(function(v){
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

        var requestHeaders = {request: Math.random()};
        ngstomp.subscribeTo('/user/topic/containers/create-status')
            .callback(handleCreateStatus)
            .withBodyInJson()
            .bindTo($scope)
            .connect();

        $scope.submitNewContainer = function () {
            $log.debug('submitting', this);
            $scope.creating = true;
            $scope.pullImageDetails = {};
            $scope.pullImageMessages = [];
            $scope.ackEula = true;  // form submission implies ack

            var request = {
                ackEula: $scope.ackEula,
                name: this.name,
                port: this.choosePort ? this.port : 0,
                type: this.type,
                version: this.version,
                assets: []
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
            if (this.enablePublic) {
                request.visibleToPublic = this.enablePublic;
            }
            if (this.enableWorld) {
                request.assets.push({
                    category: 'WORLD',
                    id: $scope.choices.selectedWorld.id
                });
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
            ngstomp.send('/app/containers/create', request, requestHeaders);
        }

        $scope.suggestMods = function(input) {
            return Mods.query({
                id: '_suggest',
                mcversion: $scope.version,
                type: $scope.type,
                input: input}).$promise;
        };

        $scope.suggestWorlds = function(input) {
            $scope.worlds = Assets.suggest({
                q: input,
                category: 'WORLD'
            });
        };

        $scope.createButton = function() {
            if ($scope.creating) {
                return 'Creating...';
            }
            else {
                return $scope.ackEula ? 'Create' : 'Accept Minecraft EULA and Create';
            }
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

        function handleCreateStatus(message) {
            var createStatus = message.body;
            $scope.createStatus = createStatus;

            switch (createStatus.state) {
                case 'ERROR':
                    Alerts.error(createStatus.details, true);
                    $scope.creating = false;
                    break;

                case 'READY':
                    close();
                    break;

                case 'PULL':
                    if (createStatus.pullDetails && createStatus.pullDetails.imageId) {
                        if (createStatus.pullDetails.total == 0) {
                            delete $scope.pullImageDetails[imageId];
                        }
                        else {
                            var imageId = createStatus.pullDetails.imageId;
                            var imageDetails = $scope.pullImageDetails[imageId];
                            if (imageDetails == undefined) {
                                imageDetails = {};
                                $scope.pullImageDetails[imageId] = imageDetails;
                            }

                            imageDetails.status = createStatus.details;
                            imageDetails.current = createStatus.pullDetails.current;
                            imageDetails.total = createStatus.pullDetails.total;
                        }
                    }
                    break;
            }
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
            $scope.selectedWorld = undefined;
            $scope.enablePublic = false;
            $scope.ackEula = false;
            $scope.worlds = [];

        }

    })

;