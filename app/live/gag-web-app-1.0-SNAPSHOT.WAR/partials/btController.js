'use strict';

angular
    .module('app')
    .controller(
        'btController',
        [
            '$scope',
            '$location',
            '$route',
            'commonTools',
            'createUpdateTools',
            function($scope, $location, $route, commonTools, createUpdateTools) {
              commonTools.getGestures().then(function(response) {
                $scope.gestures = response;
              }, function(response) {
                $scope.alerts.push({
                  type : 'danger',
                  title : 'Error ' + response.status,
                  msg : response.statusText
                });
              });

              $scope.alerts = angular.copy(createUpdateTools.getAlerts());
              createUpdateTools.deleteAlerts();

              $scope.closeAlert = function(index) {
                $scope.alerts.splice(index, 1);
              };

              $scope.ble = {
                device : {
                    name: "GAGGM",
                    serviceUUID: "6e400001-b5a3-f393-e0a9-e50e24dcca9e",
                    writeCharUUID: "6e400002-b5a3-f393-e0a9-e50e24dcca9e",
                    notifyCharUUID: "6e400003-b5a3-f393-e0a9-e50e24dcca9e"
                },
                current : null,
                currentDataLines : [
                        {
                        id: "0",
                        t: "0",
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

              $scope.log = function(msg){
              console.log("msg");
                  $('#LogMessages table tr:last').after(
                    "<tr><td>" +  new Date().toLocaleTimeString() + "</td><td>" + msg + "</td></tr>"
                  );
                  console.log(msg);
              }

               /* Utils */

               // This function keeps calling "toTry" until promise resolves or has
               // retried "max" number of times. First retry has a delay of "delay" seconds.
               // "success" is called upon success.
               $scope.ble.exponentialBackoff = function (max, delay, toTry, success, fail) {
                  toTry().then(result => success(result))
                  .catch(_ => {
                    if (max === 0) {
                      return fail();
                    }
                    $scope.ble.time('Retrying in ' + delay + 's... (' + max + ' tries left)');
                    setTimeout(function() {
                      $scope.ble.exponentialBackoff(--max, delay * 2, toTry, success, fail);
                    }, delay * 1000);
                  });
               }

               $scope.ble.time = function (text) {
                  $scope.log('[' + new Date().toJSON().substr(11, 8) + '] ' + text);
               }

               $scope.ble.convertNumberToFinger = function(nmb){
                 // THUMB, INDEX, MIDDLE, RING, LITTLE, WRIST;
                 switch(nmb){
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
                   case 5:
                     return "WRIST";
                   default:
                    return "NONE";
                 }
                 return "NONE";
               }

               $scope.ble.connect = function() {
                   $scope.ble.exponentialBackoff(5 /* max retries */, 2 /* seconds delay */,
                    function toTry() {
                      $scope.ble.time('Connecting to Bluetooth Device... ');
                      return $scope.ble.current.gatt.connect();
                    },
                    function success() {
                      $scope.log('> Bluetooth Device connected.');
                    },
                    function fail() {
                      $scope.ble.time('Failed to reconnect.');
                    });
               }

               $scope.ble.onDisconnected = function() {
                    $scope.log('> Bluetooth Device disconnected');
                    $scope.ble.connect();
               }

               $scope.ble.showReceivedValue = function (value, timeNow, timeDiff) {
                  //console.log(value);
                  $scope.log(value);
                  /*$('#content table tr:last').after(
                    "<tr><td>" + timeNow + "</td><td>" + timeDiff + "</td><td>" + value + "</td></tr>"
                  );*/
               }

               $scope.ble.str2ab = function (str) {
                  var buf = new ArrayBuffer(str.length*2); // 2 bytes for each char
                  var bufView = new Uint8Array(buf);
                  for (var i=0, strLen=str.length; i < strLen; i++) {
                    bufView[i] = str.charCodeAt(i);
                  }
                  return buf;
               }

               $scope.ble.ab2str = function (buf) {
                  return String.fromCharCode.apply(null, new Uint8Array(buf));
               }

               $scope.ble.bytesToInt = function(byte1, byte2) {
                 var number = ((byte1 << 8) | byte2);
                 var buffer = new ArrayBuffer(4);
                 var view = new DataView(buffer);
                 number = number / 16384.0;

                 if (number >= 2){
                   number = -4 + number;
                 }
                 return number;
               }

               $scope.ble.convert = function(data){
                    // g.e  [42, 153, 4, 6, 143, 197, 31, 231, 246, 2, 242, 0, 0, 13, 10]
                    // g.e  [*, counter, finger, (6, 143), (197, 31), (231, 246), (2, 242), 0, 0, 13, 10]
                    //let received = $scope.ble.ab2str(data.currentTarget.value.buffer);

                    let received = new Uint8Array(data.currentTarget.value.buffer);

                    let hand = received[0];
                    let finger = $scope.ble.convertNumberToFinger(received[2]);
                    let quatA = $scope.ble.bytesToInt(received[3],received[4]);

                    let quatX = $scope.ble.bytesToInt(received[5],received[6]);
                    let quatY = $scope.ble.bytesToInt(received[7],received[8]);
                    let quatZ = $scope.ble.bytesToInt(received[9],received[10]);

                    var jsonMessage = {
                      // "id": null,
                      "t": $.now(),
                      // TODO dynamic
                      "gid": 2,
                      "qA": quatA,
                      "qX": quatX,
                      "qY": quatY,
                      "qZ": quatZ,
                      "aX": 1,
                      "aY": 1,
                      "aZ": 1,
                      "p": finger,
                      //"mX": 1,
                      //"mY": 1,
                      //"mZ": 1
                    };
                    return jsonMessage;
               }

               $scope.connect2BLE = function(){
                  console.log("connect2BLE");
                  //$scole.log($scope.ble);

                  let serviceUuid = $scope.ble.device.serviceUUID;
                  if (serviceUuid.startsWith('0x')) {
                    serviceUuid = parseInt(serviceUuid);
                  }

                  let name = $scope.ble.device.name;

                  let characteristicWriteUuid = $scope.ble.device.writeCharUUID;
                  if (characteristicWriteUuid.startsWith('0x')) {
                    characteristicWriteUuid = parseInt(characteristicWriteUuid);
                  }
                  let characteristicNotifyUuid = $scope.ble.device.notifyCharUUID;
                  if (characteristicNotifyUuid.startsWith('0x')) {
                    characteristicNotifyUuid = parseInt(characteristicNotifyUuid);
                  }

                  $scope.log('Requesting Bluetooth Device...');

                  let promise = new Promise(
                    function(resolve, reject) {
                      if($scope.ble.current) {
                        $scope.ble.connect();
                        // TODO fix return values and reconnect ...
                        console.log($scope.ble.current);
                        resolve($scope.ble.current.gatt);
                      } else {
                        resolve(
                          navigator.bluetooth.requestDevice({ filters: [{services: [serviceUuid], name: [name]}]}).then(device => {
                            $scope.log('Connecting to GATT Server...');
                            $scope.ble.current = device;
                            $scope.ble.current.addEventListener('gattserverdisconnected', $scope.ble.onDisconnected);
                            return $scope.ble.current.gatt.connect();
                          })
                        );
                      }
                    }
                  )

                  promise.then(server => {
                    $scope.log('Getting Service...');
                    return server.getPrimaryService(serviceUuid);
                  })
                  .then(service => {
                    $scope.log('Getting Characteristics...');
                    if (characteristicWriteUuid) {
                      // Get all characteristics that match this UUID.
                      console.log(service);
                      // this requires both characteristics...
                      return Promise.all([service.getCharacteristics(characteristicWriteUuid), service.getCharacteristics(characteristicNotifyUuid)]);
                    }
                    // Get all characteristics.
                    return service.getCharacteristics();
                  })
                  .then(characteristics => {
                    $scope.log('> Characteristics: ' +
                      characteristics.map(c => c[0].uuid + " ("+(c[0].properties.write === true ? "WRITE" : (c[0].properties.notify === true ? "NOTIFY":"?"))+")").join('\n' + ' '.repeat(19)));
                      console.log(characteristics);
                      $scope.ble.bluetoothDeviceWriteChar = characteristics[0][0];
                      $scope.ble.bluetoothDeviceNotifyChar = characteristics[1][0];
                      $scope.ble.timeNow = $.now();

                      $scope.ble.bluetoothDeviceNotifyChar.addEventListener("characteristicvaluechanged", async function(ev){
                        var received = false;
                        if(!(received = $scope.ble.convert(ev))) {return;}

                        $scope.ble.timeNow = $.now();

                        var timeDiff = $scope.ble.timeNow - $scope.ble.timeNotifyLast;
                        if($scope.ble.lastTS){
                          var difference = ev.timeStamp - $scope.ble.lastTS;
                        }
                        $scope.ble.showReceivedValue(received, $scope.ble.timeNow, timeDiff);
                        $scope.ble.lastTS = ev.timeStamp;
                        $scope.ble.timeNotifyLast = $scope.ble.timeNow;

                        console.log("trying to push dataline:");
                        if(received.p === "WRIST"){
                            // TODO
                            return;
                        }
                        var jsonStr = JSON.stringify(received);
                        console.log(jsonStr);
                        commonTools.createFingerDataLine(jsonStr).then(function (response) {
                           console.log("response");
                           console.log(response);
                        }, function (response) {
                           //$scope.alerts.push({type: 'danger', title: 'Error ' + response.status, msg: response.statusText});
                           console.log("response Error");
                           console.log(response);
                        });

                      });
                      $scope.ble.bluetoothDeviceNotifyChar.startNotifications();
                  })
                  .catch(error => {
                    $scope.log('Argh! ' + error);
                  });
                }


            } ]);
