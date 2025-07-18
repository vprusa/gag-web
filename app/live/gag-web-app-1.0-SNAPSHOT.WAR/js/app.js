'use strict';

/**
 * @author Vojtěch Průša (prusa.vojtech@gmail.com)
 */
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
    getGestureInteresting: function (id) {
      return $http.get("/gagweb/api/dataline/interesting/" + id).then(function (response) {
        return response.data;
      });
    },
    createFilteredGesture: function (userAlias, filtered) {
      return $http.post("/gagweb/api/gesture/" + userAlias + "/" + filtered).then(function (response) {
        return response.data;
      });
    },
    createGesture: function (userAlias) {
      return $http.post("/gagweb/api/gesture/create/" + userAlias).then(function (response) {
        return response.data;
      });
    },
    createGestureFrom: function (oldGestureId, newAlias, dataLineIds) {
      return $http.post("/gagweb/api/gesture/from/" + oldGestureId + "/" + newAlias, dataLineIds).then(function (response) {
        return response.data;
      });
    },
    clearGesture: function (id) {
      return $http.post("/gagweb/api/gesture/clear/" + id).then(function (response) {
        return response.data;
      });
    },
    deleteGesture: function (id) {
      return $http.delete("/gagweb/api/gesture/" + id).then(function (response) {
        return response.data;
      });
    },
    setGestureActive: function (id, active) {
      return $http.put("/gagweb/api/gesture/setGestureActive/" + id+"/"+(!active ? 0 : 1)).then(function (response) {
        return response.data;
      });
    },
    setGestureShouldMatch: function (id, shouldMatch) {
      return $http.put("/gagweb/api/gesture/setGestureShouldMatch/" + id+"/"+shouldMatch).then(function (response) {
        return response.data;
      });
    },
    getGestureDetailData: function (id) {
      return $http.get("/gagweb/api/dataline/gesture/" + id).then(function (response) {
        return response.data;
      });
    },
    deleteDataLine: function (ids) {
      // TODO ids is list of ids to be deleted
      return $http.post("/gagweb/api/dataline/delete/", ids).then(function (response) {
        return response.data;
      });
    },
    createFingerDataLine: function (data) {
      return $http.post("/gagweb/api/fingerdataline", data).then(function (response) {
        return response.data;
      });
    },
    getSensorOffset: function (handDevice, position, sensorType) {
      return $http.get("/gagweb/api/sensoroffset/specific/" + handDevice + "/" + position + "/" + sensorType
      // return $http.get("/gagweb/api/sensoroffset/specific/" + 'proto_3_right' + "/" + position + "/" + sensorType
      ).then(function (response) {
        return response.data;
      });
    },
    setSensorOffset: function (handDevice, position, sensorType, value) {
      return $http.post("/gagweb/api/sensoroffset/specific/" + handDevice + "/" + position + "/" + sensorType, value
      ).then(function (response) {
        return response.data;
      });
    },
  };
}]).service('createUpdateTools', function () {
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
        .when('/recognize', {templateUrl: 'partials/recognizeGestures.html', controller: 'RecognizeGesturesController'})
        .when('/recognize2', {templateUrl: 'partials/recognizeGestures2.html', controller: 'RecognizeGesturesController2'})
      // .when('/recognize2', {templateUrl: 'partials/recognizeGestures2.html', controller: 'RecognizeGesturesController2'})
      // if devel  ..
      .when('/toolstests', {templateUrl: 'partials/tools.tests.html', controller: 'ToolsControllerTests'})
      .otherwise({redirectTo: '/'});
  }]);
