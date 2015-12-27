angular.module('Mccy.NewContainerCtrl', [
        'Mccy.services',
        'ngFileUpload'
    ])
    .controller('NewContainerCtrl', function ($scope, $log, Upload, Containers, Alerts) {

        reset();

        $scope.submitNewContainer = function () {
            $log.debug('submitting', this);

            var request = {
                ackEula: this.ackEula,
                name: this.name,
                port: this.port,
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

            if ($scope.worldFile) {
                Upload.upload({
                    url: '/api/uploads/worlds',
                    data: {file: $scope.worldFile}
                }).then(function (response) {
                    Alerts.info('World Uploaded', 'World file was uploaded successfully');

                    request.world = response.data.value;
                    $log.debug("Upload available at", request.world);
                    Containers.save(request, handleSuccess, handleRequestError);
                });
            }
            else {
                Containers.save(request, handleSuccess, handleRequestError);
            }

            close();
        };

        $scope.cancelNewContainer = function () {
            close();
        };

        function extractTagText(tag) {
            return tag.text;
        }

        function handleSuccess(value) {
            Alerts.success('The request was submitted succesfully', true);
        }

        function handleRequestError(httpResponse) {
            Alerts.error(httpResponse.data.message, true);
        }

        function close() {
            reset();
            $scope.$emit('closeNewContainerArea');
        }

        function reset() {
            $scope.name = undefined;
            $scope.port = 25565;
            $scope.type = 'VANILLA';
            $scope.version = 'LATEST';
            $scope.enableOperators = false;
            $scope.ops = [];
            $scope.enableWhitelist = false;
            $scope.whitelist = [];
            $scope.enableIcon = false;
            $scope.iconUrl = undefined;
            $scope.enableWorld = false;
            $scope.worldUrl = undefined;
            $scope.ackEula = false;
            $scope.worldUploaded = false;
            $scope.worldFile = undefined;
        }

    })

;