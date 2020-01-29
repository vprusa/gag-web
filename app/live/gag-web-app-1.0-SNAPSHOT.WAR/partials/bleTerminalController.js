'use strict';

angular
  .module('app')
  .controller(
    'BLETerminalController', [
      '$scope',
      '$location',
      '$route',
      'commonTools',
      'createUpdateTools',
      'BLETools',
      function ($scope, $location, $route, commonTools, createUpdateTools, BLETools) {
        commonTools.getGestures().then(function (response) {
          $scope.gestures = response;
        }, function (response) {
          $scope.alerts.push({
            type: 'danger',
            title: 'Error ' + response.status,
            msg: response.statusText
          });
        });
        $scope.ble = BLETools;

        $scope.terminalMessages = [
          {cmd: 'ExampleCmd', output: 'ExampleCmd\'s output'}
        ];

        $scope.sendCmd = function() {
          console.log("sendCmd");
          var cmd = $('#terminalInput').val();
          $('#terminalInput').val('');
          BLETools.sendCmd(cmd).then(function(data){
          $scope.pushToTerminal(data);
          }).catch(function(err){
            $scope.pushToTerminal(err);
          });
        };
        $scope.pushToTerminal = function(data){
          console.log("pushToTerminal");
          console.log(data);
          $scope.terminalMessages.push(data);
          $scope.$apply();
        };
      }]);
