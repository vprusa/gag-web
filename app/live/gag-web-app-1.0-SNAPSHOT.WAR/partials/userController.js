'use strict';
/*
angular.module('app').controller('UserController',
		['$scope', '$location', '$route', 'commonTools', 'createUpdateTools', 
			function ($scope, $location, $route, commonTools, createUpdateTools) {
			$scope.title = '(Gyro-Accelerometric|Gestures Automation) Glove!';
		} ]);
*/

angular.module('app')
    .controller('UserController', ['$scope', '$location', '$route', 'commonTools', 'createUpdateTools', 
    	function ($scope, $location, $route, commonTools, createUpdateTools) {
        commonTools.getCurrentUserDetail().then(function (response) {
            $scope.user = response;
            //console.log(response);
        }, function (response) {
            $scope.alerts.push({type: 'danger', title: 'Error '+ response.status, msg: response.statusText});
        });
        
        $scope.alerts = angular.copy(createUpdateTools.getAlerts());
        createUpdateTools.deleteAlerts();

        $scope.closeAlert = function(index) {
            $scope.alerts.splice(index, 1);
        };
        
        commonTools.getCurrentUserDetail().then(function (response) {
            $scope.user = response;
            console.log(response);
        }, function (response) {
            $scope.alerts.push({type: 'danger', title: 'Error '+ response.status, msg: response.statusText});
        });
        
        $scope.alerts = angular.copy(createUpdateTools.getAlerts());
        createUpdateTools.deleteAlerts();

        $scope.closeAlert = function(index) {
            $scope.alerts.splice(index, 1);
        };
        
    }]);
