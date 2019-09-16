'use strict';

angular
    .module('app')
    .controller(
        'NewGestureController',
        [
            '$scope',
            '$location',
            '$route',
            'commonTools',
            'createUpdateTools',
            function($scope, $location, $route, commonTools, createUpdateTools) {
              commonTools.getGestures().then(function(response) {
                $scope.gestures = response;
              }, function(response) {
                $scope.alerts.push({
                  type : 'danger',
                  title : 'Error ' + response.status,
                  msg : response.statusText
                });
              });

              $scope.alerts = angular.copy(createUpdateTools.getAlerts());
              createUpdateTools.deleteAlerts();

              $scope.closeAlert = function(index) {
                $scope.alerts.splice(index, 1);
              };

                $scope.newGesture = {
                    name: "test",
                    currentDataLine: {},
                    recordingInfo: {
                        isRecording: false,
                        startTime: 0,
                        recordingTime: 0,
                        recordingLength: 0
                    }
                };

                $scope.record = function(){
                    $scope.newGesture.recordingInfo.isRecording = true;
                }

                $scope.stop = function(){
                    $scope.newGesture.recordingInfo.isRecording = false;
                }

            } ]);
