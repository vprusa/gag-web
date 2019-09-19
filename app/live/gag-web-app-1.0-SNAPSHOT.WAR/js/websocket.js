/**
 * Edited by vprusa
 */
'use strict';

/*angular.module('app').controller(   'websocketController',  [
/        '$scope',
        '$location',
        '$rootScope',

        function($scope, $location, $rootScope) {
        */
angular.module('app').factory('WSTools', function(/*$rootScope*/) {
    let ws = {};
    //ws.dataLines = [];
    ws.state = "IDLE";
    ws.state = "PREPARE_REPLAYING";
    //ws.state = "REPLAYING";
    //ws.state = "RECORDING";

    ws.gestureId = "";

    ws.currentDataLines = [];

    /*ws.currentDataLine = function(msg) {
        $rootScope.websocketSession.send(msg);
    };*/

    //ws.selectedGestureDetail.player = {data: []};

    ws.onMessage = function(evt) {
        console.log(evt);
        //selectedGestureDetail.player.data
        var data = JSON.parse(evt.data);
        //ws.dataLines.length = 0;
        //angular.forEach(dataLine, function(item) {
        //  ws.dataLines.push(item);
        //});
        //ws.selectedGestureDetail.player.data.push(data) ;
        //ws.gestureId = "";
        //ws.$apply();
    };
    // ws.init = function() {
        if (!ws.websocketSession) {
             console.log("init");
             let wsProtocol = window.location.protocol == "https:" ? "wss" : "ws";
             ws.websocketSession = new WebSocket(wsProtocol + '://'
                 + document.location.host + '/gagweb/datalinews');
             //ws.websocketSession = $rootScope.websocketSession;
             //$rootScope.websocketSession.onmessage = ws.onMessage;
             ws.websocketSession.onmessage = ws.onMessage;

             console.log(ws.websocketSession);
           }
    // };

    ws.destroy = function() {
        console.log("destroy");
        if ($rootScope.websocketSession) {
          $rootScope.websocketSession.close();
        }
    };

    ws.test = function() {
        var jsonMessage = {
          "t": $.now(),
          // TODO dynamic
          "gid": 2,
          "qA": 0,
          "qX": 0,
          "qY": 0,
          "qZ": 0,
          "aX": 1,
          "aY": 1,
          "aZ": 1,
          "p": "MIDDLE",
          //"magX": 1,
          //"magY": 1,
          //"magZ": 1
        };

        function sendDataAndWait(counter, data){
            console.log(data);
            ws.currentDataLines = [data];
            console.log(ws.currentDataLines);

            var jsonStr = JSON.stringify(data);
            console.log(jsonStr);

            $rootScope.websocketSession.send(jsonStr);
            if(counter > 0){
                setTimeout(function(){sendDataAndWait(--counter, data)}, 20);
            }
        }

        sendDataAndWait(5, jsonMessage);
    }


    return ws;
//} ]);
} );
