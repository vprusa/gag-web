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

        let log = function (msg) {
          //console.log("msg");
          //    console.log(msg);
          $('#LogMessages table tr:last').after(
              "<tr><td>" + new Date().toLocaleTimeString() + "</td><td>" + msg + "</td></tr>"
          );
        };
        $scope.log = log;

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
          actionOptions: ['append', 'override'],
          hand: "1"
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
          WSTools.setState(WSTools.reqStates.STOP, $scope.data.currentGesture);
          $scope.data.recordingInfo.isRecording = false;
          $scope.data.currentGesture.id = -1;
          $scope.$apply();
        };

        $scope.onSendMessage = function (data) {
          var dataLine = JSON.parse(data);
          //console.log(dataLine);
          $scope.vis.updateVisFromDataLine(dataLine);
          $scope.$apply();
        };

        WSTools.onSendMessage = $scope.onSendMessage;


        $scope.autoRecordConfig = {
          expectedTime: 5000,
          countTo: 10,
          indexFrom: 1,
          delay: 2000,
        };

        $scope.autoRecordState = {
          currentIndex: $scope.autoRecordConfig.indexFrom,
          countdown: $scope.autoRecordConfig.expectedTime,
          delay: $scope.autoRecordConfig.delay,
          isAutoRecording: false,
          recordingInterval: null
        };

        const delay = ms => new Promise(resolve => setTimeout(resolve, ms));

        $scope.toggleAutoRecord = function () {
          if (!$scope.autoRecordState.isAutoRecording) {
            $scope.autoRecordState.isAutoRecording = true;
            $scope.autoRecordState.currentIndex = $scope.autoRecordConfig.indexFrom;
            $scope.startAutoRecord();
          } else {
            $scope.autoRecordState.isAutoRecording = false;
            clearInterval($scope.autoRecordState.recordingInterval);
          }
        };

        $scope.stopAutoRecord = function () {
          $scope.autoRecordState.isAutoRecording = false;
          clearInterval($scope.autoRecordState.recordingInterval);
          $scope.autoRecordState.currentIndex = $scope.autoRecordConfig.indexFrom;
          $scope.log("Auto Recording stopped and reset.");
        };

        $scope.startAutoRecord = async function () {
          let recordedIds = [];

          while ($scope.autoRecordState.isAutoRecording && $scope.autoRecordState.currentIndex <= $scope.autoRecordConfig.countTo) {
            $scope.data.currentGesture.userAlias = $scope.data.currentGesture.userAlias.split('_')[0] + '_' + $scope.autoRecordState.currentIndex;

            $scope.record();
            $scope.autoRecordState.countdown = $scope.autoRecordConfig.expectedTime;

            let delayRatio = 100;
            await new Promise(resolve => {
              $scope.autoRecordState.recordingInterval = setInterval(() => {
                $scope.$apply(() => $scope.autoRecordState.countdown-=delayRatio);
                if ($scope.autoRecordState.countdown <= 0) {
                  clearInterval($scope.autoRecordState.recordingInterval);
                  resolve();
                }
              }, delayRatio);
            });

            $scope.stop();
            $scope.log(`Gesture ${$scope.data.currentGesture.userAlias} recorded with ID: ${$scope.data.currentGesture.id}`);

            recordedIds.push($scope.data.currentGesture.id);

            // Enhanced countdown during delay before next recording
            $scope.autoRecordState.countdown = $scope.autoRecordConfig.expectedTime + $scope.autoRecordState.delay;
            while ($scope.autoRecordState.countdown > $scope.autoRecordConfig.expectedTime) {
              await delay(delayRatio);
              $scope.$apply(() => $scope.autoRecordState.countdown-=delayRatio);
            }

            $scope.autoRecordState.currentIndex++;
          }

          if ($scope.autoRecordState.currentIndex > $scope.autoRecordConfig.countTo) {
            $scope.autoRecordState.isAutoRecording = false;
            $scope.log(`Auto Recording completed. Gestures recorded: ${recordedIds.join(', ')}`);
          }
        };


      }]);
