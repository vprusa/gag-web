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
            $rootScope.websocketSession.send($scope.selectedGestureDetail.selectedGesture);
          };
          
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
          
          /*
           * $scope.getUserString = function (item) { var result =
           * item.users[0].firstName; if (item.users.length > 1) { result += '
           * +'+ (item.users.length - 1) +' other recommend'; } else { result += '
           * recommends'; } return result; }; $scope.artistDetail = function
           * (id) { $location.path('/artistDetail/' + id); }
           */

        } ]);
