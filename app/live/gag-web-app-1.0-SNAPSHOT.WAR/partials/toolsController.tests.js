'use strict';

angular
  .module('app')
  .controller(
    'ToolsControllerTests', [
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

        let simpleTest = function(){
          console.log("simpleTest");
          return true;
        };

        let simpleTestFail = function(){
          console.log("simpleTestFail");
          return false;
        };

        let simpleTestRand = function(){
          let res = Math.random() < 0.5;
          console.log("simpleTestRand: " + res);
          return res;
        };

        $scope.tests = {
          "simpleTest": simpleTest,
          "simpleTest2": simpleTestFail,
          "simpleTest3": simpleTestRand
        };

        $scope.testsResults = {};

        $scope.execTests = function() {
          for(var id in $scope.tests) {
            var testRes = $scope.tests[id]();
            $scope.testsResults[id] = testRes;
            //console.log(k, testRes);
          }
        };
        $scope.execTests();

        $scope.execTest = function(id) {
          console.log("execTest: " + id);
          let fun = $scope.tests[id];
          $scope.testsResults[id] = fun();
          console.log($scope.testsResults[id]);

          // TODO apply only for selected test
          // $scope.$apply();
          // $scope.$digest();
          //$scope.$evalAsync();
        }

      }]);
