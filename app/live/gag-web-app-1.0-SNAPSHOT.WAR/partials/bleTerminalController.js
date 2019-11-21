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
          {cmd: 'ExampleCmd', output: 'ExampleCmd\'s output'},
        ];

        $scope.sendCmd = function () {
          console.log("sendCmd");
          var cmd = $('#terminalInput').val();
          $('#terminalInput').val('');
          BLETools.sendCmd(cmd);
          console.log(cmd);
          $scope.terminalMessages.push({cmd: cmd, output: ''});
        }
      }]);
