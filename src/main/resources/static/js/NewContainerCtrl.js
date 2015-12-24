angular.module('Mccy.NewContainerCtrl',[
        'ngFileUpload',
        'toaster'
    ])
    .controller('NewContainerCtrl', function($scope, $log, Upload, toaster, Containers){

        reset();

        $scope.submitNewContainer = function () {
            console.log('submitting', this);

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
                }).then(function(response){
                    toaster.pop('info', 'World Uploaded', 'World file was uploaded successfully', 2000);

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

        $scope.cancelNewContainer = function() {
            close();
        };

        function extractTagText(tag) {
            return tag.text;
        }

        function handleSuccess(value) {
            toaster.pop('success', 'Success', 'The request was submitted succesfully');
            $scope.$emit('reloadContainers');
        }

        function handleRequestError(httpResponse) {
            toaster.pop('error', 'Request Failed', httpResponse.data.message);
            $scope.$emit('reloadContainers');
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