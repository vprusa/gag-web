'use strict';

/**
 * @author Vojtěch Průša (prusa.vojtech@gmail.com)
 */
angular.module('app').controller(
    'RecognizeGesturesController2', [
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
        };/**
         * @author Vojtěch Průša (prusa.vojtech@gmail.com)
         */

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
          if ($scope.isFakingBLE()) {
            dataLine.timestamp == null;
            $scope.fakingState = fakingStates.DONE;
          }
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
          FAKING: 'faking',
          DONE: 'done'
        };

        $scope.fakingState = fakingStates.IDLE;
        $scope.fakeData = {};

        $scope.fakingLoop = function (curIndex) {
          if ($scope.isIDLEBLE()) {
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

        // $scope.stopFakingBLE = function () {
        //   $scope.fakingState = fakingStates.IDLE;
        // };

        $scope.isFakingBLE = function () {
          return $scope.fakingState == fakingStates.FAKING;
        };
        $scope.isIDLEBLE = function () {
          return $scope.fakingState == fakingStates.IDLE;
        };

        $scope.isDoneBLE = function () {
          return $scope.fakingState == fakingStates.DONE;
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

        let log = function (msg) {
          //console.log("msg");
          //    console.log(msg);
          $('#LogMessages table tr:last').after(
              "<tr><td>" + new Date().toLocaleTimeString() + "</td><td>" + msg + "</td></tr>"
          );
        };
        WSTools.log = log;

        WSTools.onMessage = function (evt) {
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
          // console.log(msgObjs);

          if (msg.includes(startActionStr.replace(/\s/g, ''))) {
            // ACK ..
            console.log("setState(WSTools.reqStates.RECOGNIZE)");
            WSTools.setState(WSTools.reqStates.RECOGNIZE);
            // console.log(WSTools.state);
            // console.log($scope.isRecognizing());
            // $scope.$apply();
          } else if (Array.isArray(msgObjs) && msgObjs.length > 0 && 'data' in msgObjs[0] && 'gest' in msgObjs[0]) {
            // if at least 1 item exists
            var recognizedGestures = msgObjs;
            // console.log('recognized!');

            // TODO improve (iterate over all gestures*matches is terrible)
            // TODO also consider disabling this shady state variable called 'changed'
            $scope.gestures = $scope.gestures.map(function (e) {
              // console.log(e);
              e.recognized = false;
              for (let [key, value] of Object.entries(recognizedGestures)) {
                let recognizedGesture = value;
                if (e.id == recognizedGesture.gest.id) {
                  console.log("recognizedGesture");
                  console.log(recognizedGesture);
                  WSTools.log(" Recognized gesture: id: " + recognizedGesture.gest.id + ", alias: '" + recognizedGesture.gest.userAlias + "', threshold: " + recognizedGesture.gest.shouldMatch);
                  if (!e.recognized) {
                    changed = true;
                  }
                  // e.recognized = new Date().getTime();
                  e.recognized = true;
                  // TODO will this (setTimeout{$scope.$apply();}) not make a double trouble later?
                  setTimeout(function(){
                    e.recognized = false;
                    $scope.$apply();
                  }, 1000);
                } else {
                  if (e.recognized) {
                    changed = true;
                  }
                }
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
            // }
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

        $scope.saveShouldMatch = function(item) {
          var newValue = parseFloat(item.shouldMatch);
          if (isNaN(newValue)) {
            console.log("Please enter a valid number.");
            // item.shouldMatchInput = item.shouldMatch; // revert input
            return;
          }

          commonTools.setGestureShouldMatch(item.id, newValue).then(function(response){
            item.shouldMatch = newValue;
            console.log("Value saved successfully!");
          }, function(error){
            console.log("Error saving value: " + error.statusText);
            // item.shouldMatchInput = item.shouldMatch; // revert input on failure
          });
        };

        // tests
        $scope.testAutomation = {
          refGestureId: 86,
           inputGestureIds: [61, 62, 63, 64, 65, 67, 68, 69, 70, 71]
          // inputGestureIds: [41]
        };

        $scope.isAutomatedTesting = false;
        const delay = ms => new Promise(resolve => setTimeout(resolve, ms));

        $scope.runAutomatedTest = async function () {
          if (!$scope.testAutomation.refGestureId || !$scope.testAutomation.inputGestureIds.length) {
            alert('Please select a gesture to activate and at least one gesture to replay.');
            return;
          }

          // $scope.isAutomatedTesting = true;

          // Log all currently active gestures
          console.log("Active gestures:", $scope.gestures.filter(g => g.active).map(g => g.id));

          // Deactivate all currently active gestures
          const deactivatePromises = $scope.gestures.filter(g => g.active)
              .map(g => $scope.switchActivateGesture(g.id, true));
          await Promise.all(deactivatePromises);

          // Activate the selected gesture
          await $scope.switchActivateGesture($scope.testAutomation.refGestureId, false);

          $scope.stopRecognizing();
          await delay(2000); // Wait 1 second after recognition starts

          for (let gestureId of $scope.testAutomation.inputGestureIds) {
              log("Replaying gesture: " + gestureId)
              $scope.fakeSelectedGestureId = gestureId;
              $scope.startRecognizing();
              await delay(2000); // Wait 1 second after recognition starts

              $scope.startFakingBLE();

              while ($scope.isFakingBLE()) {
                await delay(1000); // Check every second
              }

              await delay(2000); // Additional wait after faking completes
              $scope.stopRecognizing();

              await delay(2000); // Wait 1 second before next gesture
            }
          // }
          // $scope.isAutomatedTesting = false;

        };
      }]);
