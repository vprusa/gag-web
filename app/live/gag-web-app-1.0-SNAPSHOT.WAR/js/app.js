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
        }
       
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