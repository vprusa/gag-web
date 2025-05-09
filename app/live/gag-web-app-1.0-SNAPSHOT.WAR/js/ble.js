'use strict';

/**
 * @author Vojtěch Průša (prusa.vojtech@gmail.com)
 */
angular.module('app').factory('BLETools', ['WSTools', function (WSTools) {
  let ble = {
    device: {
      name: "GAGGM",
      serviceUUID: "6e400001-b5a3-f393-e0a9-e50e24dcca9e",
      writeCharUUID: "6e400002-b5a3-f393-e0a9-e50e24dcca9e",
      notifyCharUUID: "6e400003-b5a3-f393-e0a9-e50e24dcca9e"
    },
    isConnected: false,
    current: null,
    currentDataLines: [
      /* {
      id: "",
      t: "",
      p: "",
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
      }*/
    ]
  };

  let log = function (msg) {
    //console.log("msg");
    //    console.log(msg);
    $('#LogMessages table tr:last').after(
      "<tr><td>" + new Date().toLocaleTimeString() + "</td><td>" + msg + "</td></tr>"
    );
  };
  ble.log = log;

  /* Utils */
  // This function keeps calling "toTry" until promise resolves or has
  // retried "max" number of times. First retry has a delay of "delay" seconds.
  // "success" is called upon success.
  ble.exponentialBackoff = function (max, delay, toTry, success, fail) {
    toTry().then(result => success(result))
      .catch(_ => {
        if (max === 0) {
          return fail();
        }
        ble.time('Retrying in ' + delay + 's... (' + max + ' tries left)');
        setTimeout(function () {
          ble.exponentialBackoff(--max, delay * 2, toTry, success, fail);
        }, delay * 1000);
      });
  };

  ble.time = function (text) {
    log('[' + new Date().toJSON().substr(11, 8) + '] ' + text);
  };

  ble.convertNumberToFinger = function (nmb) {
    // THUMB, INDEX, MIDDLE, RING, LITTLE, WRIST;
    switch (nmb) {
      case 0:
        return "THUMB";
      case 1:
        return "INDEX";
      case 2:
        return "MIDDLE";
      case 3:
        return "RING";
      case 4:
        return "LITTLE";
        // return "WRIST";
      case 5:
          return "WRIST";
      // case 6:
      //   return "WRIST";
      default:
        return "NONE";
    }
    return "NONE";
  };

  ble.connect = function () {
    ble.exponentialBackoff(5 /* max retries */, 2 /* seconds delay */,
      function toTry() {
        ble.time('Connecting to Bluetooth Device... ');
        return ble.current.gatt.connect();
      },
      function success() {
        log('> Bluetooth Device connected.');
      },
      function fail() {
        ble.time('Failed to reconnect.');
      });
  };

  ble.onDisconnected = function () {
    log('> Bluetooth Device disconnected');
    ble.connect();
  };

  ble.showReceivedValue = function (value, timeNow, timeDiff) {
    //console.log(value);
    log(value);
    /*$('#content table tr:last').after(
      "<tr><td>" + timeNow + "</td><td>" + timeDiff + "</td><td>" + value + "</td></tr>"
    );*/
  };

  ble.str2ab = function (str) {
    var buf = new ArrayBuffer(str.length * 2); // 2 bytes for each char
    var bufView = new Uint8Array(buf);
    for (var i = 0, strLen = str.length; i < strLen; i++) {
      bufView[i] = str.charCodeAt(i);
    }
    return buf;
  };

  ble.ab2str = function (buf) {
    return String.fromCharCode.apply(null, new Uint8Array(buf));
  };

  ble.bytesToInt = function (byte1, byte2) {
    // TODO
    var number = ((byte1 << 8) | byte2);
    var buffer = new ArrayBuffer(4);
    var view = new DataView(buffer);
    number = number / 16384.0;

    if (number >= 2) {
      number = -4 + number;
    }
    return number;
  };
  ble.convert = function (data, gestureId) {
    // g.e  [42, 153, 4, 6, 143, 197, 31, 231, 246, 2, 242, 0, 0, 13, 10]
    // g.e  [*, counter, finger, (6, 143), (197, 31), (231, 246), (2, 242), 0, 0, 13, 10]
    //let received = ble.ab2str(data.currentTarget.value.buffer);

    let received = new Uint8Array(data.currentTarget.value.buffer);
    // let hand = received[0] == '*'.charCodeAt(0) ? 'RIGHT' : 'LEFT';
    let hand = received[0] == '*'.charCodeAt(0) ? 0 : 1;
    let finger = ble.convertNumberToFinger(received[2]);

    /*
    let quatA = ble.bytesToInt(received[3],received[4]);
    let quatX = ble.bytesToInt(received[5],received[6]);
    let quatY = ble.bytesToInt(received[7],received[8]);
    let quatZ = ble.bytesToInt(received[9],received[10]);
    */

    let quatA = ble.bytesToInt(received[3], received[4]);
    let quatX = -ble.bytesToInt(received[5], received[6]);
    let quatY = ble.bytesToInt(received[7], received[8]);
    let quatZ = -ble.bytesToInt(received[9], received[10]);

    // if isWrist?
    if ( finger == "WRIST" ){
      var json_Message = {
        // "id": null,
        "t": $.now(),
        "gid": gestureId,
        "qA": quatA,
        "qX": quatX,
        "qY": quatY,
        "qZ": quatZ,
        "aX": 1,
        "aY": 1,
        "aZ": 1,
        "p": finger,
        "h": hand,
        "mX": 1,
        "mY": 1,
        "mZ": 1
      };
      if (received[2] == "5" || received[2] == "6") {
        // console.log(json_Message);
      }
      return json_Message;
    }else if (finger == "NONE"){
    }else{
      var json_Message = {
        // "id": null,
        "t": $.now(),
        // TODO dynamic
        "gid": gestureId,
        "qA": quatA,
        "qX": quatX,
        "qY": quatY,
        "qZ": quatZ,
        "aX": 1,
        "aY": 1,
        "aZ": 1,
        "p": finger,
        "h": hand
        //"mX": 1,
        //"mY": 1,
        //"mZ": 1
      };

      return json_Message;
    }
  };
  ble.reconnectCounter = 0;
  /**
   * returns Promise with resolved data {cmd: cmd, reply: reply})
   */
  ble.sendCmd = (cmd) => {
    let cmdPromise = new Promise((resolve, reject) => {
      /* stuff using cmd */
      let reply;
      // TODO proper check?
      if(typeof ble.bluetoothDeviceWriteChar != 'undefined'){
//        ble.bluetoothDeviceWriteChar.writeValue("c00\r\n");
        /*
         * set
         * - O1gx9
         * - O3gz5000
         * get
         * - o1gx0
         */
        var cmdToSend = cmd;
        if(/*cmd.length >= 4 && */ cmd.charAt(0) === 'O' || cmd.charAt(0) === 'a') {
            var nmb = parseInt(cmd.substr(4));
            console.log("nmb");
            console.log(nmb);
            var byte1 = (nmb & (255));
            var byte2 = nmb;
            byte2 = byte2>>8;
            byte2 = byte2 & (255);
            console.log("byte1: " + byte1 + " byte2: " + byte2);
            cmdToSend = cmd.substr(0,4) + String.fromCharCode(byte2) +  String.fromCharCode(byte1);
            console.log("cmdToSend");
            console.log(cmdToSend);
        }  //elseif(cmd.length < 4) {}
        ble.bluetoothDeviceWriteChar.writeValue(ble.str2ab("C"+cmdToSend+"\r\n"))
        .catch(error => {
          console.log("err");
          console.log(error);
//          ble.bluetoothDeviceWriteChar = ;
          if(++ble.reconnectCounter < 2){
            ble.connect2BLE();
            ble.sendCmd(cmd);
          }else{
            ble.reconnectCounter=0;
          }
        });
      }
      if (typeof reply != 'undefined') {
        resolve({cmd: cmd, reply:reply});
      } else {
        reject({cmd: cmd, reply:'Error: no reply!'});
      }
    }).then(response => {
      console.log("response");
      console.log(response);
    }).catch(error => {
      console.log("err");
      console.log(error);
    });
    return cmdPromise;
  };


  ble.connect2BLE = function () {
    console.log("connect2BLE");
    //log(ble);
    WSTools.init();

    let serviceUuid = ble.device.serviceUUID;
    if (serviceUuid.startsWith('0x')) {
      serviceUuid = parseInt(serviceUuid);
    }

    let name = ble.device.name;

    let characteristicWriteUuid = ble.device.writeCharUUID;
    if (characteristicWriteUuid.startsWith('0x')) {
      characteristicWriteUuid = parseInt(characteristicWriteUuid);
    }
    let characteristicNotifyUuid = ble.device.notifyCharUUID;
    if (characteristicNotifyUuid.startsWith('0x')) {
      characteristicNotifyUuid = parseInt(characteristicNotifyUuid);
    }

    log('Requesting Bluetooth Device...');

    let serverPromise = new Promise(
      function (resolve, reject) {
        if (ble.current) {
          ble.connect();
          // TODO fix return values and reconnect ...
          console.log(ble.current);
          resolve(ble.current.gatt);
        } else {
          resolve(
            navigator.bluetooth.requestDevice({filters: [{services: [serviceUuid], name: [name]}]}).then(device => {
              log('Connecting to GATT Server...');
              ble.current = device;
              ble.current.addEventListener('gattserverdisconnected', ble.onDisconnected);
              return ble.current.gatt.connect();
            })
          );
        }
      }
    );

    serverPromise.then(server => {
      log('Getting Service...');
      ble.server - server;
      return server.getPrimaryService(serviceUuid);
    }).then(service => {
      log('Getting Characteristics...');
      if (characteristicWriteUuid) {
        // Get all characteristics that match this UUID.
        console.log(service);
        // this requires both characteristics...
        ble.isConnected = !ble.isConnected;
        return Promise.all([service.getCharacteristics(characteristicWriteUuid),
          service.getCharacteristics(characteristicNotifyUuid)]);
      }
      // Get all characteristics.
      return service.getCharacteristics();
    }).then(characteristics => {

      log('> Characteristics: ' +
        characteristics.map(c => c[0].uuid + " (" +
          (c[0].properties.write === true ? "WRITE" : (c[0].properties.notify === true ? "NOTIFY" : (c[0].properties.indicate === true ? "INDICATE" : "?"))) + ")")
          .join('\n' + ' '.repeat(19)));
      console.log(characteristics);
      ble.bluetoothDeviceWriteChar = characteristics[0][0];
      ble.bluetoothDeviceNotifyChar = characteristics[1][0];
      ble.timeNow = $.now();
      console.log("ble.bluetoothDeviceNotifyChar");
      console.log(ble.bluetoothDeviceNotifyChar);

      //ble.bluetoothDeviceNotifyChar.addEventListener("characteristicvaluechanged", async function (ev) {
      //});
      var onNotif = function (ev) {
        //console.log("characteristicvaluechanged");
        var received = false;
        // TODO move most of this method to WSTools?
//        console.log(received);

        if (!(received = ble.convert(ev, WSTools.gestureId))) {
          return;
        }

        ble.timeNow = $.now();

        var timeDiff = ble.timeNow - ble.timeNotifyLast;
        if (ble.lastTS) {
          var difference = ev.timeStamp - ble.lastTS;
        }

        ble.lastTS = ev.timeStamp;
        ble.timeNotifyLast = ble.timeNow;
        var jsonStr = JSON.stringify(received);
        WSTools.sendMessage(jsonStr);
      };

      //ble.bluetoothDeviceNotifyChar.oncharacteristicvaluechanged = onNotif;
      ble.bluetoothDeviceNotifyChar.addEventListener("characteristicvaluechanged", onNotif);
//        console.log(ble.bluetoothDeviceNotifyChar);

      ble.bluetoothDeviceNotifyChar.startNotifications();
    }).catch(error => {
      log('Argh! ' + error);
      console.log(error);
    });
  };

  // TODO move here code from btController.js
  // or create custom library for it that would be included here
  return ble;
//});
}]);
