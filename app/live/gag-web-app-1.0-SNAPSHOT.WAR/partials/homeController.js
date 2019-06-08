'use strict';

angular.module('app').controller('HomeController',
		[ '$scope', 'commonTools', function($scope, commonTools) {
			$scope.title = '(Gyro-Accelerometric|Gestures Automation) Glove!';
		} ]);
