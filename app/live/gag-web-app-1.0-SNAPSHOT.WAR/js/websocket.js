/**
 * Created by Martin Styk on 18.04.2017.
 */
'use strict';

angular.module('app')
    .controller('websocketController', ['$scope', '$location', '$rootScope', function ($scope, $location, $rootScope) {
        $scope.dataLines = [];
        $scope.dataLineId = "";

        $scope.currentDataLine = function (msg) {
            $rootScope.websocketSession.send(msg);
        };
        
        $scope.onMessage = function (evt) {
            var songData = JSON.parse(evt.data);
            $scope.dataLines.length = 0;
            angular.forEach(dataLine, function (item) {
                $scope.dataLines.push(item);
            });
            $scope.dataLineId = "";
            $scope.$apply();
        };

        this.$onInit = function () {
            if (!$rootScope.websocketSession) {
                $rootScope.websocketSession = new WebSocket('ws://' + document.location.host +'/api/dataline');
                $rootScope.websocketSession.onmessage = $scope.onMessage;
            }
        };
        
        // this.$onDestroy = function () {
        //     if ($rootScope.websocketSession) { $rootScope.websocketSession.close(); }
        // };

        /*
        $scope.getUserString = function (item) {
            var result = item.users[0].firstName;
            if (item.users.length > 1) {
                result += ' +'+ (item.users.length - 1) +' other recommend';
            } else {
                result += ' recommends';
            }
            return result;
        };
        $scope.artistDetail = function (id) {
            $location.path('/artistDetail/' + id);
        }*/

    }]);
