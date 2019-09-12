/**
 * Edited by vprusa
 */
'use strict';

angular.module('app').controller(
    'websocketController',
    [
        '$scope',
        '$location',
        '$rootScope',
        function($scope, $location, $rootScope) {
          $scope.dataLines = [];
          $scope.gestureId = "";

          $scope.currentDataLine = function(msg) {
            $rootScope.websocketSession.send(msg);
          };

          $scope.playSelectedGesture = function() {
            //console.log("playSelectedGesture");
            console.log("playSelectedGesture");
            $rootScope.websocketSession.send('{"action":"replay", "gestureId": '
              + $scope.selectedGestureDetail.selectedGesture + '}');
          };

          $scope.test = function() {
            var jsonMessage = {
              "t": $.now(),
              // TODO dynamic
              "gid": 2,
              "qA": 0,
              "qX": 0,
              "qY": 0,
              "qZ": 0,
              "aX": 1,
              "aY": 1,
              "aZ": 1,
              "p": "MIDDLE",
              //"magX": 1,
              //"magY": 1,
              //"magZ": 1
            };

            var jsonStr = JSON.stringify(jsonMessage);
            console.log(jsonStr);

            function sendDataAndWait(counter, data){
                console.log(data);
                $rootScope.websocketSession.send(data);
                if(counter > 0){
                    setTimeout(function(){sendDataAndWait(--counter, data)}, 20);
                }
            }
            sendDataAndWait(5, jsonStr);

          }
          
          $scope.onMessage = function(evt) {
            console.log(evt);
            //selectedGestureDetail.player.data
            var data = JSON.parse(evt.data);
            //$scope.dataLines.length = 0;
            //angular.forEach(dataLine, function(item) {
            //  $scope.dataLines.push(item);
            //});

            $scope.selectedGestureDetail.player = { data: data };
            //$scope.gestureId = "";
            $scope.$apply();
          };

          this.$onInit = function() {
            if (!$rootScope.websocketSession) {
              var wsProtocol = window.location.protocol == "https:" ? "wss"
                  : "ws";
              $rootScope.websocketSession = new WebSocket(wsProtocol + '://'
                  + document.location.host + '/gagweb/datalinews');
              $rootScope.websocketSession.onmessage = $scope.onMessage;
            }
          };

          this.$onDestroy = function() {
            if ($rootScope.websocketSession) {
              $rootScope.websocketSession.close();
            }
          };

        } ]);
