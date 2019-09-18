'use strict';


angular
  .module('app')
  .directive('slideToggle', function() {
  return {
    restrict: 'A',
    scope:{},
    controller: function ($scope) {},
    link: function(scope, element, attr) {
      element.bind('click', function() {
        var $slideBox = angular.element(attr.slideToggle);
        $slideBox.stop().slideToggle(parseInt(attr.slideToggleDuration));
      });
    }
  };
});

angular
    .module('app')
    .controller(
        'BLEController', [
            '$scope',
            '$location',
            '$route',
            'commonTools',
            'createUpdateTools',
            'BLETools',
            function($scope, $location, $route, commonTools, createUpdateTools, BLETools) {
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

              $scope.ble = BLETools;

            }]);
