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
            'BLETools',
            function($scope, $location, $route, commonTools, createUpdateTools, BLETools) {
              commonTools.getGestures().then(function(response) {
                $scope.gestures = response;
              }, function(response) {
                $scope.alerts.push({
                  type : 'danger',
                  title : 'Error ' + response.status,
                  msg : response.statusText
                });
              });

              $scope.ble = BLETools;

              $scope.alerts = angular.copy(createUpdateTools.getAlerts());
              createUpdateTools.deleteAlerts();

              $scope.closeAlert = function(index) {
                $scope.alerts.splice(index, 1);
              };

              $scope.data = {
                currentGesture : {
                  id: -1,
                  userAlias: "test",
                  dateCreated: -1,
                  user: -1,
                  isFiltered: -1
                },
                currentDataLine: {},
                recordingInfo: {
                  isRecording: false,
                  startTime: 0,
                  recordingTime: 0,
                  recordingLength: 0
                },
                info: "TODO live 3D model",
                gesturesList: [
                    //{id: "-1", userAlias: "", dateCreated: -1, isFiltered: 0},
                ],
                //selectedGesture: {-1: 'Select existing gesture'},
                //					<option selected value="-1">Select existing gesture</option>
                selectedGesture:-1,
                actionOptions: [ 'append', 'override' ]
              };

              $scope.onSelectChange = function(){
             //   $scope.data.selectedGesture = -1;
              }

              commonTools.getGestures().then(function(gestures){
                console.log(gestures);
                $scope.data.gesturesList = gestures;
                $scope.data.selectedGesture = -1;
              });

              $scope.record = function(){
                // new gesture or add to existing
                if($scope.data.currentGesture.id == -1) {
                  console.log($scope.data.currentGesture.userAlias);
                  commonTools.createGesture($scope.data.currentGesture.userAlias, 0).then(function(gesture){
                     $scope.data.currentGesture = gesture;});
                } else {

                }

                $scope.data.recordingInfo.isRecording = true;
            }

            $scope.stop = function(){
                $scope.data.recordingInfo.isRecording = false;
            }

            } ]);
