angular.module('Mccy.directives', [])

    .directive('mccyServerConnection', function($timeout, cTimeouts) {

        function controller($scope, $element, $attrs) {
            $scope.$watchGroup(['hostIp', 'hostPort'], function (newValues) {
                $scope.serverAddress = newValues[0] + ':' + newValues[1];
            });

            $scope.triggerShowCopied = function() {
                $scope.showCopied = true;
                $timeout(function () {
                    $scope.showCopied = false;
                }, cTimeouts.copiedTooltipHide);

            }
        }

        return {
            restrict: 'A',
            templateUrl: 'ng-bits/server-connection.html',
            scope: {
                hostIp: '=',
                hostPort: '='
            },
            controller: controller
        }
    })
;