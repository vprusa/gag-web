'use strict';

angular.module('app').factory('WSTools', function (/*$rootScope*/) {
  var ws = {};
  //ws.dataLines = [];
  // TODO add state automate, encapsulate variables, create access methods, etc.
  // state automate would have states:
  // waiting for input from WS or app
  ws.state = "IDLE";

  // when waiting for replaying preparations to complete
  //ws.state = "PREPARE_REPLAYING";
  // when replaying data
  //ws.state = "REPLAYING";
  // when waiting for replaying preparations to complete, e.g. delete DataLines for Override option in gesture
  //ws.state = "PREPARE_RECORDING";
  // when recording data
  //ws.state = "RECORDING";
  // when replaying and recording, idk if necessary
  // this one may be implemented far in the future for Use Case "shadowing gesture":
  // visualization of current live gesture recording against
  // already existing gesture replaying at the same time...
  //ws.state = "REPLAYING_AND_RECORDING";

  ws.reqStates = {
    STOP: 0,
    RECORD: 1,
    REPLAY: 2,
    RECOGNIZE: 3
  };

  ws.innerStates = {
    IDLE: 0,
    PREPARE_REPLAYING: 1,
    REPLAYING: 2,
    PREPARE_RECORDING: 3,
    RECORDING: 4,
    RECOGNIZING: 5
  };
  ws.state = ws.innerStates.IDLE;

  // TODO rename to requestState?
  ws.setState = function (newState, gestureId) {
    switch (newState) {
      case ws.reqStates.RECORD:
        if (typeof gestureId == 'undefined') {
          console.log("For WSTools state RECORD gestureId can not be undefined");
          break;
        }
        //ws.state = ws.reqStates.IDLE;
        ws.state = ws.innerStates.PREPARE_RECORDING;
        // TODO preparations
        ws.gestureId = gestureId;
        ws.state = ws.innerStates.RECORDING;
        break;
      case ws.reqStates.REPLAY:
        break;
      case ws.reqStates.STOP:
        ws.state = ws.innerStates.IDLE;
        break;
      case ws.reqStates.RECOGNIZE:
        ws.state = ws.innerStates.RECOGNIZING;
        break;
      default:
        console.log("WSTools unknown state: ");
        console.log(newState);
    }
  };

  ws.gestureId = "";

  // TODO refactor this callback used in visualization (remove/rename/idk/wtf)
  /**
   * This should be used for any dealing with message before possible sending
   * e.g. used in visualization
   */
  ws.onSendMessage = function (data) {
    // override
  };

  ws.sendMessage = function (msg) {
    if (ws.checkStates.isRecording() || ws.checkStates.isRecognizing()) {
    // if (ws.checkStates.isRecording()) {
      ws.onSendMessage(msg);
      //console.log("sending message");
      ws.websocketSession.send(msg);
    } else if (msg.includes("type")) {
      console.log("Sending Action message: " + msg);
      // its an action
      ws.websocketSession.send(msg);
    } else {
      // visualization callback
      ws.onSendMessage(msg);
    }
    //console.log("not sending message");
  };

  ws.checkStates = {
    isRecording: function () {
      return ws.state == ws.innerStates.RECORDING;
    },
    isReplaying: function () {
      return ws.state == ws.innerStates.REPLAYING;
    },
    isRecognizing: function () {
      return ws.state == ws.innerStates.RECOGNIZING;
    }
  };

  ws.gestureId = "";

  ws.currentDataLines = [];

  ws.setOnMessage = function (f) {
    ws.websocketSession.onmessage = f;
  };
  ws.onMessage = function (evt) {
    //console.log(evt);
    //var data = JSON.parse(evt.data);
  };
  ws.init = function () {
    // console.log(ws.websocketSession);
    if (!ws.websocketSession || ws.websocketSession.readyState == 3) {
      console.log("init");
      let wsProtocol = window.location.protocol == "https:" ? "wss" : "ws";
      ws.websocketSession = new WebSocket(wsProtocol + '://'
        + document.location.host + '/gagweb/datalinews');
      // ws.websocketSession = $rootScope.websocketSession;
      // $rootScope.websocketSession.onmessage = ws.onMessage;
      ws.websocketSession.onmessage = ws.onMessage;
      console.log(ws.websocketSession);
    }
  };

  ws.close = function () {
    console.log("close");
    if (ws.websocketSession) {
      ws.websocketSession.close();
    }
  };

  /*
  ws.test = function () {
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

    function sendDataAndWait(counter, data) {
      console.log(data);
      ws.currentDataLines = [data];
      console.log(ws.currentDataLines);

      var jsonStr = JSON.stringify(data);
      console.log(jsonStr);

      ws.websocketSession.send(jsonStr);
      if (counter > 0) {
        setTimeout(function () {
          sendDataAndWait(--counter, data)
        }, 20);
      }
    }

    sendDataAndWait(5, jsonMessage);
  };
  */

  ws.isMessageDataLine = function (message) {
    // TODO
    return true;
  };

  return ws;
});
