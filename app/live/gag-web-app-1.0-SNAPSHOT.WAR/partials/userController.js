'use strict';
<<<<<<< HEAD
=======
/*
angular.module('app').controller('UserController',
		['$scope', '$location', '$route', 'commonTools', 'createUpdateTools', 
			function ($scope, $location, $route, commonTools, createUpdateTools) {
			$scope.title = '(Gyro-Accelerometric|Gestures Automation) Glove!';
		} ]);
*/


>>>>>>> Fixes and UI changes

angular.module('app')
    .controller('UserController', ['$scope', '$location', '$route', 'commonTools', 'createUpdateTools', 
    	function ($scope, $location, $route, commonTools, createUpdateTools) {
<<<<<<< HEAD
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
        
=======
        commonTools.getSongsForUser().then(function (response) {
            $scope.songs = response;
        }, function (response) {
            $scope.alerts.push({type: 'danger', title: 'Error '+ response.status, msg: response.statusText});
        });

>>>>>>> Fixes and UI changes
    }]);
