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
}]).factory('BLETools',function() {
    let ble = {
       device : {
           name: "GAGGM",
           serviceUUID: "6e400001-b5a3-f393-e0a9-e50e24dcca9e",
           writeCharUUID: "6e400002-b5a3-f393-e0a9-e50e24dcca9e",
           notifyCharUUID: "6e400003-b5a3-f393-e0a9-e50e24dcca9e"
       },
       isConnected: false,
       current : null,
       currentDataLines : [
               {
               id: "0",
               t: 0,
               p: "INDEX",
               qA: "",
               qX: "",
               qY: "",
               qZ: "",
               aX: "",
               aY: "",
               aZ: "",
               mX: "",
               mY: "",
               mZ: ""
               }
           ]
     };

     // TODO move here code from btController.js
     // or create custom library for it that would be included here
    return ble;
}).service('createUpdateTools', function () {
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

            /*.when('/albumsOverview', {templateUrl: 'partials/albumsOverview.html', controller: 'albumsOverviewCtrl'})
            .when('/artistDetail/:id?', {templateUrl: 'partials/artistDetail.html', controller: 'artistDetailCtrl'})
            .when('/artistsOverview', {templateUrl: 'partials/artistsOverview.html', controller: 'artistsOverviewCtrl'})
            .when('/editAlbum/:id?', {templateUrl: 'partials/editAlbum.html', controller: 'editAlbumCtrl'})
            .when('/editArtist/:id?', {templateUrl: 'partials/editArtist.html', controller: 'editArtistCtrl'})
            .when('/editSong/:id?', {templateUrl: 'partials/editSong.html', controller: 'editSongCtrl'})
            .when('/songsOverview', {templateUrl: 'partials/songsOverview.html', controller: 'songsOverviewCtrl'})
            .when('/myLibrary', {templateUrl: 'partials/myLibrary.html', controller: 'myLibraryCtrl'})
            .when('/addSong', {templateUrl: 'partials/addSong.html', controller: 'addSongCtrl'})
            */
            .otherwise({redirectTo: '/'});
    }]);
