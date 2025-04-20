'use strict';

/**
 * @author Vojtěch Průša (prusa.vojtech@gmail.com)
 */
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


        $scope.ws = WSTools;
        // $scope.wsFake = WSToolsFake;
        $scope.vis = VisTools;
        // WSTools.setEndpoint(WSTools.endpointSpecifications.RECOGNIZER);
        // WSTools.init();
        $scope.vis.numberOfHandsPairs = 1;



        $scope.lastRecognizedGesture = {id: null, count: 0};

          $scope.recognitionResults = {};

          $scope.captureRecognizedGesture = function(recognizedGesture) {
            let inputGestureId = $scope.fakeSelectedGestureId;
            let gestureId = recognizedGesture.gest.id;
            let recogGestureId = gestureId;

            // Initialize structure if necessary
            $scope.recognitionResults[recogGestureId] = $scope.recognitionResults[recogGestureId] || {};
            $scope.recognitionResults[recogGestureId][inputGestureId] = $scope.recognitionResults[recogGestureId][inputGestureId] || { count: 0 };

            // Increment count for recognized gesture
            $scope.recognitionResults[recogGestureId][inputGestureId].count += 1;

            // Update last recognized gesture state
            if ($scope.lastRecognizedGesture.id === gestureId) {
              $scope.lastRecognizedGesture.count += 1;
            } else {
              $scope.lastRecognizedGesture = { id: gestureId, count: 1 };
            }

            console.log("Recognized gesture captured: id: " + gestureId + ", alias: '" + recognizedGesture.gest.userAlias + "'" + " counter: " + $scope.lastRecognizedGesture.count);
          };


        $scope.vis.loadOffsets = function(handDeviceId) {
          const sensors = ['wrist', 'thumb', 'index', 'middle', 'ring', 'little'];
          const axes = ['x', 'y', 'z'];

          // let promises = [];

          ['left', 'right'].forEach(hand => {
            sensors.forEach((sensor, position) => {
              // promises.push(
                  commonTools.getSensorOffset(handDeviceId[hand], position, 0).then(function (response) {
                    axes.forEach(axis => {
                      const key = `${hand}-${sensor}-${axis}-num`;
                      $scope.vis.visualizationOffsets[key] = response[axis];
                    });
                  }, function (response) {
                    $scope.alerts.push({
                      type: 'danger',
                      title: 'Error ' + response.status,
                      msg: response.statusText
                    });
                  })
              // );
            });
          });

          // return $q.all(promises);
        };

        $scope.vis.storeOffsets = function(handDeviceId, offsets) {
          const sensors = ['wrist', 'thumb', 'index', 'middle', 'ring', 'little'];
          const axes = ['x', 'y', 'z'];

          // let promises = [];

          ['left', 'right'].forEach(hand => {
            sensors.forEach((sensor, position) => {
              let values = axes.map(axis => offsets[`${hand}-${sensor}-${axis}-num`]);
              // promises.push(
                  commonTools.setSensorOffset(handDeviceId[hand], position, 0, values).then(function (response) {
                    axes.forEach((axis, index) => {
                      const key = `${hand}-${sensor}-${axis}-num`;
                      $scope.vis.visualizationOffsets[key] = response[index];
                    });
                  }, function (response) {
                    $scope.alerts.push({
                      type: 'danger',
                      title: 'Error ' + response.status,
                      msg: response.statusText
                    });
                  })
              // );
            });
          });

          // return $q.all(promises);
        };


        // Controller function example in visualizationCard
        $scope.vis.storeCurrentOffsets = function() {
          const handDeviceId = {
            left: 2,  // Replace these with dynamic values as necessary
            right: 1
          };

          $scope.vis.storeOffsets(handDeviceId, $scope.vis.visualizationOffsets)
              .then(() => alert('Offsets stored successfully'))
              .catch(err => alert('Failed to store offsets: ' + err));
        };

        // Call loadOffsets during initialization to fetch existing data
        const handDeviceId = { left: 2, right: 1 };
        // $scope.vis.loadOffsets(handDeviceId);

        // $scope.gestures = response;
        // console.log("response");
        // console.log(response);
        $scope.gestures = response.map(function (e) {
          // e.recognized = true;
          e.recognized = false;

          return e;
        });

// Generate confusion matrix immediately after initialization
          $scope.generateConfusionMatrix();

          // console.log(response);
          // response
        }, function (response) {
          $scope.alerts.push({
            type: 'danger',
            title: 'Error ' + response.status,
            msg: response.statusText
          });
        });

        $scope.alerts = angular.copy(createUpdateTools.getAlerts());
        createUpdateTools.deleteAlerts();

        $scope.closeAlert = function (index) {
          $scope.alerts.splice(index, 1);
        };

        this.$onInit = function () {
          WSTools.selectedEndpoint = WSTools.endpointRecognizer;
          // WSTools.init();

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

        $scope.setActivateGesture = function (id, active) {
          commonTools.setGestureActive(id, active).then(function () {
            var index = $scope.gestures.map(function (e) {
              return e.id;
            }).indexOf(id);
            $scope.gestures[index].active = active;
          });
        };

        $scope.onSendMessage = function (data) {
          if (typeof data === "undefined") {
            return;
          }
          var dataLine = JSON.parse(data);
          if (typeof dataLine.REPLAYER !== "undefined") {
            if ($scope.isBLEFaking()) {
              console.log("Ble done: ", dataLine)
              // dataLine.timestamp == null;
              // $scope.fakingState = fakingStates.DONE;
            }
          } else {
            // if ($scope.isBLEFaking()) {
              // console.log("Ble done: ", dataLine)
              // dataLine.timestamp == null;
              // $scope.fakingState = fakingStates.DONE;
            // }
            // console.log(dataLine);
            $scope.vis.updateVisFromDataLine(dataLine);
            if (!$scope.$$phase) {
              $scope.$apply();
            }
          }
        };

        WSTools.onSendMessage = $scope.onSendMessage;
        // var startAction = '{type:"RECOGNITION",action:"START"}';
        var startAction = {type: "RECOGNITION", action: "START"};
        var startActionStr = JSON.stringify(startAction);
        $scope.startRecognizing = async function () {
          WSTools.init();
          console.log("startRecognizing");
          const delay = ms => new Promise(resolve => setTimeout(resolve, ms));
          await delay(500);
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

        // $scope.fakeLoopModifier = 1.0;
        // $scope.fakeLoopModifier = 0.5;
        // $scope.fakeLoopModifier = 0.25;
        $scope.fakeLoopModifier = 0.1;

        $scope.fakingLoop = function (curIndex) {
          if ($scope.isBLEIdle()) {
            return;
          }
          var first = $scope.fakeData[curIndex];
          // console.log(first);
          if (curIndex >= $scope.fakeData.length - 1) {
            $scope.fakingState = fakingStates.DONE;
            console.log("Done faking..." + curIndex + ">=" + $scope.fakeData.length);
            return;
          }
          $scope.vis.updateVisFromDataLine(first);
          // console.log(curIndex);
          // console.log(first);
          $scope.ws.sendMessage(JSON.stringify(first));

          if (typeof $scope.fakeData[curIndex + 1] !== "undefined") {
            var second = $scope.fakeData[curIndex + 1];
            var delay = (second.t - first.t) * $scope.fakeLoopModifier;
            var res = setTimeout($scope.fakingLoop, delay, curIndex + 1);
          }

        };

        $scope.startFakingBLE = function () {
          $scope.fakingState = fakingStates.FAKING;
          commonTools.getGestureDetailData($scope.fakeSelectedGestureId)
              .then(function (resp) {
                $scope.fakeData = resp;
                console.log($scope.fakeData);
                $scope.fakingLoop(0);
              });
        };

        // $scope.stopFakingBLE = function () {
        //   $scope.fakingState = fakingStates.IDLE;
        // };

        $scope.isBLEFaking = function () {
          return $scope.fakingState == fakingStates.FAKING;
        };
        $scope.isBLEIdle = function () {
          return $scope.fakingState == fakingStates.IDLE;
        };

        $scope.isBLEDone = function () {
          return $scope.fakingState == fakingStates.DONE;
        };


        // ----------------------------------------------------------------------------

        $scope.isRecognizing = function () {
          return WSTools.checkStates.isRecognizing();
        };

        $scope.stopRecognizing = function () {
          console.log("stopRecognizing");
          WSTools.setState(WSTools.reqStates.STOP);
          WSTools.close();
        };

        var gestureMatches = {
          current: null,
          last: null,
          all: []
        };

        let log = function (msg) {
          //console.log("msg");
          //    console.log(msg);
          // $('#LogMessages table tr:last').after(
          $('#LogMessages span').after(
              "" + new Date().toLocaleTimeString() + " " + msg + "<br>"
          );
        };
        WSTools.log = log;
        $scope.log = log;

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

                  console.log("recognizedGesture - adding to confusion matrix...");
                  $scope.captureRecognizedGesture(recognizedGesture);

                  WSTools.log("Recognized gesture: id: " + recognizedGesture.gest.id
                      + ", alias: '" + recognizedGesture.gest.userAlias
                      + "', threshold: " + recognizedGesture.gest.shouldMatch
                      + "', delay : " + recognizedGesture.gest.delay
                  );

                  if (!e.recognized) {
                    changed = true;
                  }
                  // e.recognized = new Date().getTime();
                  e.recognized = true;
                  setTimeout(function(){
                    e.recognized = false;
                    $scope.$apply();
                  }, 500);
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

        // auto test
        // http://localhost:8080/gagweb/#!/recognize?refGestureIds=75,76,77&inputGestureIds=41,42,43,44,45,46,47,48,49,50
        // http://localhost:8080/gagweb/#!/recognize?refGestureIds=85,86,87&inputGestureIds=41,42,43,44,45,46,47,48,49,50

        $scope.recognitionConfig = {
          refGestureIds: ($location.search().refGestureIds || "").split(",").map(Number),
          inputGestureIds: ($location.search().inputGestureIds || "").split(",").map(Number)
        };

        $scope.recognitionResults = {};

        const delay = ms => new Promise(resolve => setTimeout(resolve, ms));

        $scope.cellStatus = {}; // Initialize at the controller level
        $scope.resetCellStatus = function() {
          $scope.cellStatus = {}; // clears all previous coloring statuses
          $scope.recognitionResults = {};
        };
        $scope.getCellStyle = function(rowIndex, colIndex) {
          if (
              typeof $scope.confusionMatrix === "undefined" ||
              typeof $scope.confusionMatrix[rowIndex] === "undefined" ||
              typeof $scope.confusionMatrix[rowIndex][0] === "undefined" ||
              typeof $scope.confusionMatrix[0][colIndex] === "undefined"
          ) {
            return;
          }
          const refId = $scope.confusionMatrix[rowIndex][0].split(':')[0];
          const inputId = $scope.confusionMatrix[0][colIndex].split(':')[0];
          const status = $scope.cellStatus[`${refId}_${inputId}`];

          if (status === 'completed_ok') return {'background-color': 'lightgreen'};
          if (status === 'completed_0') return {'background-color': 'lightgrey'};
          if (status === 'processing') return {'background-color': 'lightorange'};
          return {};
        };

        $scope.runMultipleAutomatedTests = async function () {
          log("Starting recognition: " + $location.search().refGestureIds + " against " + $location.search().inputGestureIds);
          $scope.resetCellStatus();
          $scope.generateConfusionMatrix();
          if (!$scope.recognitionConfig.refGestureIds.length || !$scope.recognitionConfig.inputGestureIds.length) {
            alert('Please provide reference and input gestures.');
            return;
          }

          // Deactivate all currently active gestures
          console.log("Deactivating active gestures...");
          const deactivatePromises = $scope.recognitionConfig.refGestureIds
              .map(g => {
                console.log("Deactivating gesture: " + g);
                $scope.setActivateGesture(g, false);
              });
          console.log("Deactivating active gestures... - done");

          await delay(1000);
          $scope.stopRecognizing();
          await delay(1000);
          $scope.startRecognizing();
          await delay(1000);
          await Promise.all(deactivatePromises);
          for (let refGestureId of $scope.recognitionConfig.refGestureIds) {
            log("Recognizing gesture: " + refGestureId);
            await $scope.setActivateGesture(refGestureId, true);
            await delay(1000);
            console.log($scope.recognitionConfig.inputGestureIds);
            for (let inputGestureId of $scope.recognitionConfig.inputGestureIds) {
              log("Recognizing at gesture: " + inputGestureId);
              $scope.cellStatus[`${refGestureId}_${inputGestureId}`] = 'processing';
              $scope.generateConfusionMatrix();

              $scope.fakeSelectedGestureId = inputGestureId;
              // $scope.recognitionResults[refGestureId] = $scope.recognitionResults[refGestureId] || {};

              $scope.startRecognizing();
              await delay(1000);
              // console.log("1");
              $scope.startFakingBLE();
              // console.log("2");
              while ($scope.isBLEFaking()) {
                // console.log("2.1");
                await delay(1000);
                console.log("2.2");
              }

              // console.log("3");
              await delay(1000);
              $scope.stopRecognizing();
              await delay(1000);
              if (typeof $scope.recognitionResults === 'undefined'
                  || typeof $scope.recognitionResults[refGestureId] === 'undefined'
                  || typeof $scope.recognitionResults[refGestureId][inputGestureId] === 'undefined'
                  || typeof $scope.recognitionResults[refGestureId][inputGestureId] === 'undefined'
                  || typeof $scope.recognitionResults[refGestureId][inputGestureId].count === 'undefined' ) {
                $scope.cellStatus[`${refGestureId}_${inputGestureId}`] = 'completed_0';
              } else if ($scope.recognitionResults[refGestureId][inputGestureId].count > 0) {
                $scope.cellStatus[`${refGestureId}_${inputGestureId}`] = 'completed_ok';
              } else {
                console.log("idk wtf");
                console.log($scope.recognitionResults);
              }
              await delay(1000);
              $scope.lastRecognizedGesture.id = null;
              $scope.generateConfusionMatrix();
              await delay(1000);

            }
            await delay(1000);
            $scope.stopRecognizing();
            await delay(1000);
            console.log("Last recognized ID:", $scope.lastRecognizedGestureId);
            console.log("Last recognized ID:", $scope.lastRecognizedGesture.id);
            console.log("Last recognized ID:", $scope.lastRecognizedGesture.count);
            await $scope.setActivateGesture(refGestureId, false);
            await delay(1000);
            await Promise.all(deactivatePromises);
            await delay(1500);
            // $scope.lastRecognizedGestureId = null;
            //  await $scope.switchActivateGesture(refGestureId, true);
            $scope.generateConfusionMatrix();
            $scope.lastRecognizedGesture.id = null;
          }

          console.log("Recognition Results:", $scope.recognitionResults);
          log("Done recognition: " + $location.search().refGestureIds + " against " + $location.search().inputGestureIds);
          $scope.generateConfusionMatrix();
        };

        // Watch for changes in input configuration to regenerate confusion matrix
        $scope.$watch('recognitionConfig', function(newVal, oldVal) {
          if (newVal !== oldVal) {
            $scope.generateConfusionMatrix();
          }
        }, true);

        // Generate Confusion Matrix
        $scope.generateConfusionMatrix = function() {

          // Prepare headers from inputGestureIds with gesture aliases
          const headers = [];
          $scope.recognitionConfig.inputGestureIds.forEach(inputId => {
            const gesture = $scope.gestures.find(g => g.id === inputId);
            if(typeof gesture !== "undefined") {
              headers.push(`${gesture.id}: ${gesture.userAlias}`);
            }
          });

          const matrix = [headers];

          $scope.recognitionConfig.refGestureIds.forEach(refId => {
            const gesture = $scope.gestures.find(g => g.id === refId);
            if (typeof gesture === "undefined") {
              return;
            }
            const row = [`${gesture.id}: ${gesture.userAlias}`];
            $scope.recognitionConfig.inputGestureIds.forEach(inputId => {
              const count = $scope.calculateGestureMatchCount(refId, inputId);
              row.push(count);
            });
            matrix.push(row);
          });

          $scope.confusionMatrix = matrix;

          const counts = [].concat(...matrix.slice(1).map(r => r.slice(1)));
          $scope.min_count = Math.min(...counts.filter(c => c > 0));
          $scope.max_count = Math.max(...counts);

          $scope.generateGroupedConfusionMatrix();
          if (!$scope.$$phase) {
            $scope.$apply();
          }
        };

        // Corrected function to calculate matches from stored results
        $scope.calculateGestureMatchCount = function(expectedId, recognizedId) {
          return ($scope.recognitionResults[expectedId] &&
              $scope.recognitionResults[expectedId][recognizedId]) ?
              $scope.recognitionResults[expectedId][recognizedId].count : 0;
        };

        // $scope.group_regex = ".*-(?:click|switch|\\w+)_?(\\d+)$";
        // $scope.group_regex = ".*-(?:\w+)?(\d+)$";
        $scope.group_regex = "^(.+)_(\\d+)$";
        // $scope.group_regex = /^(.*?)[-_]\d+$/;


        $scope.cm_selector = "matched_count"; // Default to 'all'
        $scope.cm_count_limit = 1;
        $scope.min_count = 0;
        $scope.max_count = 0;
        $scope.groupedConfusionMatrix = [];

/*

        $scope.generateGroupedConfusionMatrix = function () {
          console.log("generateGroupedConfusionMatrix");
          if (!$scope.group_regex) {
            $scope.groupedConfusionMatrix = [];
            return;
          }

          const regex = new RegExp($scope.group_regex);
          const inputGroups = {};
          // Group input gestures by regex
          $scope.recognitionConfig.inputGestureIds.forEach(id => {
            const gesture = $scope.gestures.find(g => g.id === id);
            const match = gesture.userAlias.match(regex);
            if (match) {
              const key = match[1];
              inputGroups[key] = inputGroups[key] || [];
              inputGroups[key].push(id);
            }
          });
          console.log(inputGroups);
          const headers = [].concat(Object.keys(inputGroups));
          const matrix = [headers];

          $scope.recognitionConfig.refGestureIds.forEach(refId => {
            const refGesture = $scope.gestures.find(g => g.id === refId);
            const row = [`${refGesture.id}: ${refGesture.userAlias}`];

            Object.keys(inputGroups).forEach(groupKey => {
              const gestureIds = inputGroups[groupKey];
              const counts = gestureIds.map(inputId =>
                  ($scope.recognitionResults[refId] && $scope.recognitionResults[refId][inputId])
                      ? $scope.recognitionResults[refId][inputId].count
                      : 0
              );

              let aggregatedCount = 0;
              if ($scope.cm_selector === "all") {
                aggregatedCount = counts.every(c => c >= $scope.cm_count_limit) ? 1 : 0;
              } else if ($scope.cm_selector === "any") {
                aggregatedCount = counts.some(c => c >= $scope.cm_count_limit) ? 1 : 0;
              } else if ($scope.cm_selector === "none") {
                aggregatedCount = counts.every(c => c < $scope.cm_count_limit) ? 1 : 0;
              }

              row.push(aggregatedCount);
            });

            matrix.push(row);
          });

          console.log(matrix);

          $scope.groupedConfusionMatrix = matrix;
        };
*/


        $scope.generateGroupedConfusionMatrix = function () {
          if (!$scope.group_regex) {
            $scope.groupedConfusionMatrix = [];
            return;
          }

          let regex;
          try {
            regex = new RegExp($scope.group_regex);
          } catch(e) {
            console.error("Invalid regex:", e);
            $scope.groupedConfusionMatrix = [];
            return;
          }

          const inputGroups = {};

          // Group input gestures by regex (correctly using capturing groups)
          $scope.recognitionConfig.inputGestureIds.forEach(id => {
            const gesture = $scope.gestures.find(g => g.id === id);
            const alias = gesture.userAlias.trim();
            const match = alias.match(regex);
            const key = match ? (match[1] || alias) : alias; // match[1] if subgroup exists, else alias

            inputGroups[key] = inputGroups[key] || [];
            inputGroups[key].push(id);
          });

          const headers = [].concat(Object.keys(inputGroups));
          const matrix = [headers];

          $scope.recognitionConfig.refGestureIds.forEach(refId => {
            const refGesture = $scope.gestures.find(g => g.id === refId);
            const row = [`${refGesture.id}: ${refGesture.userAlias}`];

            Object.keys(inputGroups).forEach(groupKey => {
              const gestureIds = inputGroups[groupKey];
              const counts = gestureIds.map(inputId =>
                  ($scope.recognitionResults[refId] && $scope.recognitionResults[refId][inputId])
                      ? $scope.recognitionResults[refId][inputId].count
                      : 0
              );

              let aggregatedCount = 0;

              switch($scope.cm_selector) {
                case "all":
                  aggregatedCount = counts.every(c => c >= $scope.cm_count_limit) ? 1 : 0;
                  break;
                case "any":
                  aggregatedCount = counts.some(c => c >= $scope.cm_count_limit) ? 1 : 0;
                  break;
                case "none":
                  aggregatedCount = counts.every(c => c < $scope.cm_count_limit) ? 1 : 0;
                  break;
                case "matched_count":
                  aggregatedCount = counts.reduce((sum, c) => sum + (c >= $scope.cm_count_limit ? 1 : 0), 0);
                  break;
              }

              row.push(aggregatedCount);
            });

            matrix.push(row);
          });

          $scope.groupedConfusionMatrix = matrix;
        };

        $scope.$watchGroup(['group_regex', 'cm_selector', 'cm_count_limit', 'confusionMatrix'], $scope.generateGroupedConfusionMatrix, true);

      }]);
