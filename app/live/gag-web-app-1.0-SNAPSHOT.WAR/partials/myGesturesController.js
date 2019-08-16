'use strict';

angular
    .module('app')
    .controller(
        'MyGesturesController',
        [
            '$scope',
            '$location',
            '$route',
            'commonTools',
            'createUpdateTools',
            function($scope, $location, $route, commonTools, createUpdateTools) {
              commonTools.getGestures().then(function(response) {
                $scope.gestures = response;
              }, function(response) {
                $scope.alerts.push({
                  type : 'danger',
                  title : 'Error ' + response.status,
                  msg : response.statusText
                });
              });

              $scope.alerts = angular.copy(createUpdateTools.getAlerts());
              createUpdateTools.deleteAlerts();

              $scope.closeAlert = function(index) {
                $scope.alerts.splice(index, 1);
              };

              commonTools.getGestures().then(function(response) {
                $scope.gestures = response;
              }, function(response) {
                $scope.alerts.push({
                  type : 'danger',
                  title : 'Error ' + response.status,
                  msg : response.statusText
                });
              });

              $scope.alerts = angular.copy(createUpdateTools.getAlerts());
              createUpdateTools.deleteAlerts();

              $scope.closeAlert = function(index) {
                $scope.alerts.splice(index, 1);
              };

              $scope.selectedGestureDetail = {
                info : "selected gesture info",
                list : false,
                data : "data"
              };

              $scope.listDetail = function(id) {
                if ($scope.selectedGestureDetail.list == false) {
                  commonTools.getGestureDetailData(id).then(function(response) {
                    $scope.selectedGestureDetail.data = response;
                    $scope.selectedGestureDetail.list = true;
                  }, function(response) {
                    $scope.alerts.push({
                      type : 'danger',
                      title : 'Error ' + response.status,
                      msg : response.statusText
                    });
                  });
                } else {
                  $scope.selectedGestureDetail.list = false;
                }
              };

              $scope.selectGesture = function(id) {
                console.log("selectGesture: " + id);
                $scope.selectedGestureDetail.play = !$scope.selectedGestureDetail.play;
                $scope.selectedGestureDetail.selectedGesture = id;
              }

            } ]);
