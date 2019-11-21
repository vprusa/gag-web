'use strict';

angular
  .module('app')
  .controller(
    'MyGesturesController', [
      '$scope',
      '$location',
      '$route',
      'commonTools',
      'createUpdateTools',
      'WSTools',
      'VisTools',
      function ($scope, $location, $route, commonTools, createUpdateTools, WSTools, VisTools) {
        commonTools.getGestures().then(function (response) {
          $scope.gestures = response;
        }, function (response) {
          $scope.alerts.push({
            type: 'danger',
            title: 'Error ' + response.status,
            msg: response.statusText
          });
        });

        $scope.ws = WSTools;
        $scope.vis = VisTools;

        $scope.alerts = angular.copy(createUpdateTools.getAlerts());
        createUpdateTools.deleteAlerts();

        $scope.closeAlert = function (index) {
          $scope.alerts.splice(index, 1);
        };

        this.$onInit = function () {
          WSTools.init();
        }

        this.$onDestroy = function () {
          WSTools.destroy();
        }

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
        }

        $scope.clearGesture = function (id) {
          console.log("clearGesture: " + id);
        }

        $scope.deleteGesture = function (id) {
          console.log("deleteGesture: " + id);
        }

        $scope.onMessage = function (evt) {
          var dataLine = JSON.parse(evt.data);
          $scope.vis.updateVisFromDataLine(dataLine);
          $scope.$apply();
        };

        $scope.ws.playSelectedGesture = function () {
          if ($scope.selectedGestureDetail.selectedGesture)
            $scope.ws.init();
          $scope.ws.setOnMessage($scope.onMessage);
          console.log($scope.selectedGestureDetail.selectedGesture);
          $scope.ws.websocketSession.send('{"action":"replay", "gestureId": '
            + $scope.selectedGestureDetail.selectedGesture + '}');
        };

      }]);
