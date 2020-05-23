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
    'VisTools',
    function ($scope, $location, $route, $timeout, commonTools, createUpdateTools, WSTools, VisTools) {
      commonTools.getGestures().then(function (response) {
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
      $scope.vis = VisTools;

      $scope.vis.numberOfHandsPairs = 1;

      $scope.alerts = angular.copy(createUpdateTools.getAlerts());
      createUpdateTools.deleteAlerts();

      $scope.closeAlert = function (index) {
        $scope.alerts.splice(index, 1);
      };

      this.$onInit = function () {
        WSTools.init();
      };

      this.$onDestroy = function () {
        WSTools.destroy();
      };

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
        // if (!$scope.$$phase) {
        $scope.$apply();
        // }
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
        // console.log(msg);
        var msgObj = JSON.parse(msg);
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
            console.log($scope.gestures);
            $scope.$apply();
          }
        } else if (msg.includes(startActionStr.replace(/\s/g, ''))) {
          // ACK ..
          WSTools.setState(WSTools.reqStates.RECOGNIZE);
          // console.log(WSTools.state);
          // console.log($scope.isRecognizing());
          // $scope.$apply();
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
