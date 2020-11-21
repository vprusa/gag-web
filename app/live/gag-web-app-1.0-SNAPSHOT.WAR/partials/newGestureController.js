'use strict';

angular
  .module('app')
  .controller(
    'NewGestureController', [
      '$scope',
      '$location',
      '$route',
      'commonTools',
      'createUpdateTools',
      'BLETools',
      'WSTools',
      'VisTools',
      function ($scope, $location, $route, commonTools, createUpdateTools, BLETools, WSTools, VisTools) {
        commonTools.getGestures().then(function (response) {
          $scope.gestures = response;
        }, function (response) {
          $scope.alerts.push({
            type: 'danger',
            title: 'Error ' + response.status,
            msg: response.statusText
          });
        });

        $scope.ble = BLETools;
        $scope.ws = WSTools;

        $scope.vis = VisTools;

        $scope.vis.numberOfHandsPairs = 1;

        // WSTools.setEndpoint(WSTools.endpointSpecifications.RECORDER);
        // WSTools.init();

        this.$onInit = function () {
          WSTools.selectedEndpoint = WSTools.endpointRecorder;
          WSTools.init();
        };

        this.$onDestroy = function () {
          WSTools.destroy();
        };

        $scope.alerts = angular.copy(createUpdateTools.getAlerts());
        createUpdateTools.deleteAlerts();

        $scope.closeAlert = function (index) {
          $scope.alerts.splice(index, 1);
        };

        $scope.data = {
          currentGesture: {
            id: "",
            userAlias: "",
            dateCreated: "",
            user: "",
            isFiltered: ""
          },
          currentDataLine: {},
          recordingInfo: {
            isRecording: false,
            startTime: 0,
            recordingTime: 0,
            recordingLength: 0
          },
          info: "TODO live 3D model",
          gesturesList: [{id: "", userAlias: ""}
            //{id: "-1", userAlias: "", dateCreated: -1, isFiltered: 0},
          ],
          //selectedGesture: {-1: 'Select existing gesture'},
          //					<option selected value="-1">Select existing gesture</option>
          selectedGesture: "",
          actionOptions: ['append', 'override']
        };

        // Warning ... idk it is broken using ng-model displays additional broken option...
        $scope.onSelectChange = function () {
          // $scope.data.selectedGesture = $("#toSelectGesture option:selected").value();
          $scope.data.currentGesture = $scope.data.gesturesList[$scope.data.selectedGesture - 1];
        };

        commonTools.getGestures().then(function (gestures) {
          console.log(gestures);
          $scope.data.gesturesList = gestures;
          $scope.data.selectedGesture = "";
        });

        $scope.record = function () {
          // new gesture or add to existing
          if (!$scope.data.currentGesture.id || $scope.data.currentGesture.id == -1 || $scope.data.currentGesture.id == "") {
            console.log($scope.data.currentGesture.userAlias);
            commonTools.createGesture($scope.data.currentGesture.userAlias, 0).then(function (gesture) {
              $scope.data.currentGesture = gesture;
              WSTools.setState(WSTools.reqStates.RECORD, gesture.id);
              // TODO move logically WSTools.state instead of recordingInfo.isRecording
              // using ws.checkState.isRecording();
              $scope.data.recordingInfo.isRecording = true;
            });
          } else {
            WSTools.setState(WSTools.reqStates.RECORD, $scope.data.currentGesture.id);
            // TODO move logically WSTools.state instead of recordingInfo.isRecording
            // using ws.checkState.isRecording();
            $scope.data.recordingInfo.isRecording = true;
          }
        };

        $scope.stop = function () {
          $scope.data.recordingInfo.isRecording = false;
        };

        $scope.onSendMessage = function (data) {
          var dataLine = JSON.parse(data);
          //console.log(dataLine);
          $scope.vis.updateVisFromDataLine(dataLine);
          $scope.$apply();
        };

        WSTools.onSendMessage = $scope.onSendMessage;

      }]);
