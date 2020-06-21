'use strict';

angular.module('app').controller(
  'RecognizeGesturesController', [
    '$scope',
    '$location',
    '$route',
    '$timeout',
    'commonTools',
    'createUpdateTools',
    'WSTools',
    // 'WSTools',
    'VisTools',
    function ($scope, $location, $route, $timeout, commonTools, createUpdateTools, WSTools, /*WSToolsFake,*/ VisTools) {
      commonTools.getGestures().then(function (response) {
        // Why WSToolsFake ? I was thinking why not to have fake over WSTools , but no..
        // for testing purposes its easier to just load whole gesture with its data

        // $scope.gestures = response;
        // console.log("response");
        // console.log(response);
        $scope.gestures = response.map(function (e) {
          // e.recognized = true;
          e.recognized = false;
          return e;
        });

        // console.log(response);
        // response
      }, function (response) {
        $scope.alerts.push({
          type: 'danger',
          title: 'Error ' + response.status,
          msg: response.statusText
        });
      });

      $scope.ws = WSTools;
      // $scope.wsFake = WSToolsFake;
      $scope.vis = VisTools;
      // WSTools.setEndpoint(WSTools.endpointSpecifications.RECOGNIZER);
      // WSTools.init();
      $scope.vis.numberOfHandsPairs = 1;

      $scope.alerts = angular.copy(createUpdateTools.getAlerts());
      createUpdateTools.deleteAlerts();

      $scope.closeAlert = function (index) {
        $scope.alerts.splice(index, 1);
      };

      this.$onInit = function () {
        WSTools.selectedEndpoint = WSTools.endpointRecognizer;
        WSTools.init();

        // WSToolsFake.selectedEndpoint = WSToolsFake.endpointReplayer;
        // WSToolsFake.init();
      };

      this.$onDestroy = function () {
        WSTools.destroy();
      };

      // this.$onInit = function () {
      //   WSTools.setEndpoint(WSTools.endpointSpecifications.RECOGNIZER);
      //   WSTools.init();
      // };

      // this.$onDestroy = function () {
      // WSTools.destroy();
      // };

      $scope.selectedGestureDetail = {
        info: "Watch 3D model of hands",
        data: []
      };

      $scope.selectedGestureList = {
        display: -1,
        data: []
      };

      $scope.listDetail = function (id) {
        if ($scope.selectedGestureList.display == -1 || id != $scope.selectedGestureList.display) {
          commonTools.getGestureDetailData(id).then(function (response) {
            $scope.selectedGestureList.data = response;
            $scope.selectedGestureList.display = id;
          }, function (response) {
            $scope.alerts.push({
              type: 'danger',
              title: 'Error ' + response.status,
              msg: response.statusText
            });
          });
        } else {
          $scope.selectedGestureList.display = -1;
        }
      };

      $scope.selectGesture = function (id) {
        console.log("selectGesture: " + id);
        $scope.selectedGestureDetail.play = !$scope.selectedGestureDetail.play;
        $scope.selectedGestureDetail.selectedGesture = id;
      };

      $scope.clearGesture = function (id) {
        console.log("clearGesture: " + id);
      };

      $scope.switchActivateGesture = function (id, active) {
        commonTools.setGestureActive(id, !active).then(function () {
          var index = $scope.gestures.map(function (e) {
            return e.id;
          }).indexOf(id);
          $scope.gestures[index].active = !active;
        });
      };

      $scope.onSendMessage = function (data) {
        var dataLine = JSON.parse(data);
        // console.log(dataLine);
        $scope.vis.updateVisFromDataLine(dataLine);
        if (!$scope.$$phase) {
          $scope.$apply();
        }
      };

      WSTools.onSendMessage = $scope.onSendMessage;
      // var startAction = '{type:"RECOGNITION",action:"START"}';
      var startAction = {type: "RECOGNITION", action: "START"};
      var startActionStr = JSON.stringify(startAction);
      $scope.startRecognizing = function () {
        console.log("startRecognizing");
        WSTools.sendMessage(startActionStr);

        // $scope.$apply();
        //'{type:"RECOGNITION",action:"START"}';
        // var ACTIONS = '{type:"RECOGNITION",action:"START"}';
        // '{type:1,action:0}'
        // '{type:1}'
      };
      // ----------------------------------------------------------------------------
      // Faking
      // ----------------------------------------------------------------------------

      $scope.fakeSelectedGestureId = -1;
      $scope.fakeSelectedGestureIndex = -1;

      $scope.onFakeSelectChange = function () {
        // $scope.data.selectedGesture = $("#toSelectGesture option:selected").value();
        // $scope.fakeSelectedGestureIndex = $scope.gestures.findIndex(g => g.id == $scope.fakeSelectedGestureId);
        // $scope.fakeSelectedGesture = $scope.gestures[$scope.fakeSelectedGestureIndex];
        // console.log($scope.fakeSelectedGestureIndex);
        console.log($scope.fakeSelectedGestureId);
        // console.log($scope.gestures);
      };

      var fakingStates = {
        IDLE: 'idle',
        FAKING: 'faking'
      };

      $scope.fakingState = fakingStates.IDLE;
      $scope.fakeData = {};

      $scope.fakingLoop = function (curIndex) {
        if (!$scope.isFakingBLE()) {
          return;
        }
        var first = $scope.fakeData[curIndex];
        // console.log(first);
        if (typeof $scope.fakeData[curIndex + 1] !== "undefined") {
          var second = $scope.fakeData[curIndex + 1];
          var delay = second.t - first.t;
          var res = setTimeout($scope.fakingLoop, delay, curIndex + 1);
        }
        $scope.vis.updateVisFromDataLine(first);
        $scope.ws.sendMessage(JSON.stringify(first));
      };

      $scope.startFakingBLE = function () {
        commonTools.getGestureDetailData($scope.fakeSelectedGestureId)
          .then(function (resp) {
            $scope.fakeData = resp;
            $scope.fakingLoop(0);
            // console.log($scope.fakeData);
          });
        $scope.fakingState = fakingStates.FAKING;
      };

      $scope.stopFakingBLE = function () {
        $scope.fakingState = fakingStates.IDLE;
      };

      $scope.isFakingBLE = function () {
        return $scope.fakingState == fakingStates.FAKING;
      };


      // ----------------------------------------------------------------------------

      $scope.isRecognizing = function () {
        return WSTools.checkStates.isRecognizing();
      };

      $scope.stopRecognizing = function () {
        console.log("stopRecognizing");
        WSTools.setState(WSTools.reqStates.STOP);
      };

      var gestureMatches = {
        current: null,
        last: null,
        all: []
      };

      WSTools.onMessage = function (evt) {
        // console.log("onMessage");
        // console.log(evt);
        // var msg = JSON.stringify(JSON.parse(evt.data));
        var msg = evt.data;
        gestureMatches.last = null;

        var changed = false;
        $scope.gestures = $scope.gestures.map(function (e) {
          if (e.recognized) {
            e.recognized = false;
            changed = true;
          }
          return e;
        });
        var msgObjs = JSON.parse(msg);
        // console.log(msg);
        console.log(msgObjs);
        for (var msgObj in msgObjs) {
          // console.log(k, result[k]);
          if (typeof msgObj[0] !== 'undefined' && typeof msgObj[0].index !== 'undefined') {
            // console.log(msgObj);
            gestureMatches.last = msgObj[0];
            $scope.gestures = $scope.gestures.map(function (e) {
              // console.log(e);
              if (e.id == gestureMatches.last.g.id) {
                // console.log("recognized");
                // console.log(e);
                if (!e.recognized) {
                  changed = true;
                }
                e.recognized = true;
              } else {
                if (e.recognized) {
                  changed = true;
                }
                e.recognized = false;
              }
              return e;
            });
            if (changed) {
              // TODO why is the program get here when  recognition is running && startFaking is clicked?
              // I have some idea and the problem may have to be solved for visualization of recognized sensor
              // console.log("");
              // console.log($scope.gestures);
              $scope.$apply();
            }
          } else if (msg.includes(startActionStr.replace(/\s/g, ''))) {
            // ACK ..
            console.log("setState(WSTools.reqStates.RECOGNIZE)");
            WSTools.setState(WSTools.reqStates.RECOGNIZE);
            // console.log(WSTools.state);
            // console.log($scope.isRecognizing());
            // $scope.$apply();
          } else {
            // console.log("else");
            // console.log()
          }
        }

      };

      // faking BLE data with re-player data
      $scope.isRecognizing = function () {
        return WSTools.checkStates.isRecognizing();
      };

      $scope.stopRecognizing = function () {
        console.log("stopRecognizing");
        WSTools.setState(WSTools.reqStates.STOP);
      };

    }]);
