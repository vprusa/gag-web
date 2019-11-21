'use strict';

angular.module('app', [
    'ngRoute', 'ngFileUpload', 'ngSanitize', 'chart.js'
]).factory('commonTools', ['$http', function ($http) {
    return {
        getCurrentUser: function () {
            return $http.get("/gagweb/api/user/current").then(function (response) {
                return response.data;
            });
        },
        getCurrentUserDetail: function () {
            return $http.get("/gagweb/api/user/currentdetail").then(function (response) {
                return response.data;
            });
        },
        getGestures: function () {
            return $http.get("/gagweb/api/gesture/mine").then(function (response) {
                return response.data;
            });
        },
        getGestureDetail: function (id) {
            return $http.get("/gagweb/api/gesture/" + id).then(function (response) {
                return response.data;
            });
        },
        createGesture: function (userAlias, filtered) {
            return $http.post("/gagweb/api/gesture/" + userAlias + "/" + filtered).then(function (response) {
                return response.data;
            });
        },
        clearGesture: function (id) {
            return $http.post("/gagweb/api/gesture/" + userAlias + "/" + filtered).then(function (response) {
                return response.data;
            });
        },
        deleteGesture: function (id) {
            return $http.delete("/gagweb/api/gesture/" + id).then(function (response) {
                return response.data;
            });
        },
        getGestureDetailData: function (id) {
            return $http.get("/gagweb/api/dataline/gesture/" + id).then(function (response) {
                return response.data;
            });
        },
        createFingerDataLine: function (data){
           return $http.post("/gagweb/api/fingerdataline", data).then(function (response){
               return response.data;
           });
        }

    };
}]) .service('createUpdateTools', function () {
    var alerts = [];
    return {
        getAlerts: function () {
            return alerts;
        },
        addAlert: function (newAlert) {
            alerts.push(newAlert);
        },
        setAlerts: function (newAlerts) {
            alerts = newAlerts;
        },
        deleteAlerts: function () {
            alerts = [];
        }
    }
}).config(['$routeProvider',
    function ($routeProvider) {
        $routeProvider
            .when('/', {templateUrl: 'partials/home.html', controller: 'HomeController'})
            .when('/about', {templateUrl: 'partials/about.html'})
            .when('/user', {templateUrl: 'partials/user.html', controller: 'UserController'})
            .when('/newgesture', {templateUrl: 'partials/newGesture.html', controller: 'NewGestureController'})
            .when('/mygestures', {templateUrl: 'partials/myGestures.html', controller: 'MyGesturesController'})
            .otherwise({redirectTo: '/'});
    }]);
