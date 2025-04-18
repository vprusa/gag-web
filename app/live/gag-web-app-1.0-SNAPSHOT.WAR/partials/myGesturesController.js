'use strict';

angular
  .module('app')
  .controller(
    'MyGesturesController', [
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
          $scope.gestures = response;
        }, function (response) {
          $scope.alerts.push({
            type: 'danger',
            title: 'Error ' + response.status,
            msg: response.statusText
          });
        });

        $scope.selectedGestureList = {
          gestureId: -1,
          data: [],
          selectedGesture: -1
        };

        $scope.selectedItems = {}

        $scope.ws = WSTools;
        $scope.vis = VisTools;

        $scope.vis.numberOfHandsPairs = 1;

        $scope.alerts = angular.copy(createUpdateTools.getAlerts());
        createUpdateTools.deleteAlerts();

        $scope.closeAlert = function (index) {
          $scope.alerts.splice(index, 1);
        };



        this.$onInit = function () {
          WSTools.selectedEndpoint = WSTools.endpointReplayer;
          WSTools.init();
        };

        this.$onDestroy = function () {
          WSTools.destroy();
        };

        $scope.selectedGestureDetail = {
          info: "Watch 3D model of hands",
          data: [],
          selectedGesture: -1
        };
        $scope.saveSelectedAsNewGestureAlias = "save-as-new-";

        $scope.listDetail = function (id) {
          console.log("listDetail");
          if ($scope.selectedGestureList.gestureId == -1 || id != $scope.selectedGestureList.gestureId) {

            commonTools.getGestureDetailData(id).then(function (response) {
              console.log("listDetail - response");
              console.log(response);
              $scope.selectedGestureList.data = response;
              $scope.selectedGestureList.gestureId = id;
            }, function (response) {
              $scope.alerts.push({
                type: 'danger',
                title: 'Error ' + response.status,
                msg: response.statusText
              });
            });
          } else {
            $scope.selectedGestureList.gestureId = -1;
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

        $scope.deleteGesture = function (id) {
          commonTools.deleteGesture(id).then(function(){
              $scope.gestures = $scope.gestures.filter(function(item) {
                return item.id !== id;
              });
            });
        };

        $scope.deleteSelectedItems = function () {
          let selectedIds = $scope.selectedGestureList.data
              .filter(item => item.selected)
              .map(item => item.id);

          if (selectedIds.length === 0) return;

          $http.post("/gagweb/api/dataline/delete/", selectedIds).then(function (response) {
            // Remove deleted items from list
            $scope.selectedGestureList.data = $scope.selectedGestureList.data.filter(item => !item.selected);
          }).catch(function (error) {
            console.error("Error deleting selected data lines:", error);
          });
        };

        $scope.anyItemSelected = function () {
          return $scope.selectedGestureList.data.filter(item => item.selected).length > 0;
        };

        $scope.invertSelection = function () {
          $scope.selectedGestureList.data.forEach(item => {
            item.selected = !item.selected;
          });
        };

        $scope.deselectAll = function () {
          $scope.selectedGestureList.data.forEach(item => item.selected = false);
          $scope.lastSelectedIndex = null;
        };


        $scope.lastSelectedIndex = null;

        $scope.toggleSelection = function ($event, index) {
          if ($event.shiftKey && $scope.lastSelectedIndex !== null) {
            let start = Math.min($scope.lastSelectedIndex, index);
            let end = Math.max($scope.lastSelectedIndex, index);
            for (let i = start; i <= end; i++) {
              $scope.selectedGestureList.data[i].selected = true;
            }
          } else {
            $scope.selectedGestureList.data[index].selected = !$scope.selectedGestureList.data[index].selected;
          }

          $scope.lastSelectedIndex = index;
        };

        $scope.switchActivateGesture = function (id, active) {
          commonTools.setGestureActive(id, !active).then(function(){
            var index = $scope.gestures.map(function(e) { return e.id; }).indexOf(id);
            $scope.gestures[index].active = !active;
          });
        };

        $scope.saveSelectedAsNewGesture = function () {
          console.log("Save selected as new gesture");
          console.log($scope.saveSelectedAsNewGestureAlias);
          console.log($scope.selectedGestureList.gestureId);
          var selectedDataLineIds = $scope.selectedGestureList.data.filter(item => item.selected).map(item=>item.id);
          console.log(selectedDataLineIds);
          commonTools.createGestureFrom($scope.selectedGestureList.gestureId, $scope.saveSelectedAsNewGestureAlias, selectedDataLineIds).then(function(){
            // TODO custom feedback
            console.log("createGestureFrom");
          });
        };


        $scope.onMessage = function (evt) {
          // TODO move to websocket.js
          console.log(evt.data);
          var message = JSON.parse(evt.data);
          if($scope.ws.isMessageDataLine(message)){
            $scope.vis.updateVisFromDataLine(message);
            $scope.$apply();
          }else{
            $scope.ws.onMessage(message);
          }
        };

        let playerStates = {
          IDLE: 0,
          PLAYING: 1,
          PAUSED: 2,
          STOPPED: 3
        };

        $scope.player = {
          states: playerStates,
          state: playerStates.IDLE
        };

        $scope.ws.startPlayer = function () {
          if ($scope.selectedGestureDetail.selectedGesture) {
            commonTools.getGestureInteresting($scope.selectedGestureDetail.selectedGesture).then(function (response) {
              // console.log(response);
              $scope.vis.resetProgressBar();
              console.log(response);
              if (response.length > 1) {
                $scope.vis.currentGesture.startTime = response[0].t;
                $scope.vis.currentGesture.endTime = response[1].t;
              }
            }, function (response) {
              $scope.alerts.push({type: 'danger', title: 'Error ' + response.status, msg: response.statusText});
            });
            $scope.ws.selectedEndpoint = $scope.ws.endpointReplayer;
            $scope.ws.init();
          }

          $scope.ws.setOnMessage($scope.onMessage);
          console.log($scope.selectedGestureDetail.selectedGesture);
          $scope.ws.websocketSession.send('{"type":0, "action":0, "gestureId": '
            + $scope.selectedGestureDetail.selectedGesture + '}');

          // TODO move this after first message?
          $scope.player.state = playerStates.PLAYING;
        };

        $scope.ws.stopPlayer = function () {
          if($scope.ws.isPlaying){
            $scope.vis.resetProgressBar();
            console.log($scope.selectedGestureDetail.selectedGesture);
            $scope.ws.websocketSession.send('{"type":0, "action":3, "gestureId": '
              + $scope.selectedGestureDetail.selectedGesture + '}');
          }
          $scope.player.state = playerStates.STOPPED;
          $scope.ws.close();
        };

        $scope.ws.pausePlayer = function () {
          // TODO
          // if($scope.ws.isPlaying){
          //   $scope.ws.stopPlaying();
            $scope.ws.websocketSession.send('{"type":0, "action":1, "gestureId": '
              + $scope.selectedGestureDetail.selectedGesture + '}');
          // }
          $scope.player.state = playerStates.PAUSED;
        };

        $scope.ws.continuePlayer = function () {
          // TODO
          // if($scope.ws.isPlaying){
            //$scope.ws.stopPlaying();
            $scope.ws.websocketSession.send('{"type":0, "action":2, "gestureId": '
              + $scope.selectedGestureDetail.selectedGesture + '}');
          // }
          $scope.player.state = playerStates.PLAYING;
        };

      }]);
