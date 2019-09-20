'use strict';

angular
    .module('app')
    .controller(
        'MyGesturesController',
        [
            '$scope',
            '$location',
            '$route',
            'commonTools',
            'createUpdateTools',
            'WSTools',
            function($scope, $location, $route, commonTools, createUpdateTools, WSTools) {
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
              $scope.ws = WSTools;

              this.$onInit = function(){
                WSTools.init();
              }

              this.$onDestroy = function(){
                WSTools.destroy();
              }

              $scope.selectedGestureDetail = {
                info : "TODO: 3D model of replaying data",
                data : []
              };

              $scope.selectedGestureList = {
                display: -1,
                data : []
              };

              $scope.listDetail = function(id) {
                  if($scope.selectedGestureList.display == -1 || id != $scope.selectedGestureList.display ){
                      commonTools.getGestureDetailData(id).then(function(response) {
                        $scope.selectedGestureList.data = response;
                        $scope.selectedGestureList.display = id;
                      }, function(response) {
                        $scope.alerts.push({
                          type : 'danger',
                          title : 'Error ' + response.status,
                          msg : response.statusText
                        });
                      });
                  }else{
                    $scope.selectedGestureList.display = -1;
                  }
              };

              $scope.selectGesture = function(id) {
                console.log("selectGesture: " + id);
                $scope.selectedGestureDetail.play = !$scope.selectedGestureDetail.play;
                $scope.selectedGestureDetail.selectedGesture = id;
              }

              $scope.clearGesture = function(id) {
                  console.log("clearGesture: " + id);

              }

              $scope.deleteGesture = function(id) {
                console.log("deleteGesture: " + id);
                //commonTools.
              }

              $scope.onMessage = function(evt) {
                  //selectedGestureDetail.player.data
                  var dataLine = JSON.parse(evt.data);
                  console.log(dataLine);
                  //$scope.selectedGestureDetail.data.push(dataLine) ;
                  $scope.selectedGestureDetail.data = [dataLine];
                  $scope.$apply();
              };

              $scope.ws.websocketSession.onmessage =  $scope.onMessage;
              $scope.ws.playSelectedGesture = function() {
                  //console.log("playSelectedGesture");
                  console.log("playSelectedGesture");
                  if($scope.selectedGestureDetail.selectedGesture)
                  $scope.ws.websocketSession.send('{"action":"replay", "gestureId": '
                    + $scope.selectedGestureDetail.selectedGesture + '}');
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
                        timestamp: "0",
                        position: "1",
                        quatA: "",
                        quatX: "",
                        quatY: "",
                        quatZ: "",
                        accX: "",
                        accY: "",
                        accZ: "",
                        magX: "",
                        magY: "",
                        magZ: ""
                        }
                    ]
              };

              $scope.log = function(msg){
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
                  console.log(value);
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
                 //var number = parseInt(byte1) | parseInt(byte1)_ << 8;
                 //var number = parseInt(byte1) | parseInt(byte1)_ << 8;
                 var number = ((byte1 << 8) | byte2);
                 //console.log("number");
                 //console.log(number);
                 var buffer = new ArrayBuffer(4);
                 var view = new DataView(buffer);
                 //console.log(byte1);
                 //console.log(byte2)
                 //console.log("number2");
                 //console.log(number);

                 number = number / 16384.0;
                 //console.log("number3");
                 //console.log(number);

                 if (number >= 2){
                   number = -4 + number;
                 }
                 //console.log("number4");
                 //console.log(number);
                 return number;
               }

               $scope.ble.convert = function(data){
                    // g.e  [42, 153, 4, 6, 143, 197, 31, 231, 246, 2, 242, 0, 0, 13, 10]
                    // g.e  [*, counter, finger, (6, 143), (197, 31), (231, 246), (2, 242), 0, 0, 13, 10]
                    //let received = $scope.ble.ab2str(data.currentTarget.value.buffer);

                    let received = new Uint8Array(data.currentTarget.value.buffer);

                    //console.log(received);

                    let hand = received[0];
                    let finger = $scope.ble.convertNumberToFinger(received[2]);
                    let quatA = $scope.ble.bytesToInt(received[3],received[4]);

                    let quatX = $scope.ble.bytesToInt(received[5],received[6]);
                    let quatY = $scope.ble.bytesToInt(received[7],received[8]);
                    let quatZ = $scope.ble.bytesToInt(received[9],received[10]);

                    /*
                    console.log("quatA");
                    console.log(quatA);
                    console.log("quatX");
                    console.log(quatX);
                    console.log("quatY");
                    console.log(quatY);
                    console.log("quatZ");
                    console.log(quatZ);
                    */

// {"id":1,"timestamp":-3599000,"quatA":1.0,"quatX":1.0,"quatY":1.0,"quatZ":1.0,"accX":1,"accY":1,"accZ":1,"position":"THUMB","magX":1,"magY":1,"magZ":1}
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

                      //"magX": 1,
                      //"magY": 1,
                      //"magZ": 1
                    };
                    //console.log(jsonMessage);
                    return jsonMessage;
               }

               $scope.test = function(){
  // "id": null,
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


                    var jsonStr = JSON.stringify(jsonMessage);
                    console.log(jsonStr);
                    commonTools.createFingerDataLine(jsonStr).then(function (response) {
                       //$scope.status = "New song successfully created.";
                       //createUpdateTools.setAlerts([{type: 'success', title: 'Successful!', msg: $scope.status}]);
                       //$location.path("/songsOverview");
                       console.log("response");
                       console.log(response);
                    }, function (response) {
                       //$scope.alerts.push({type: 'danger', title: 'Error ' + response.status, msg: response.statusText});
                       console.log("response Error");
                       console.log(response);
                    });
                }

               $scope.connect2BLE = function(){
                  console.log("connect2BLE");
                  console.log($scope.ble);

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
                  /*function resolve(){
                    if ($scope.ble.current) {
                      return $scope.ble.current;
                    }
                  }*/

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
                      //timeNow = window.performance.now();
                      $scope.ble.timeNow = $.now();
                      console.log("Time timeNow: " + $scope.ble.timeNow);

                      $scope.ble.bluetoothDeviceNotifyChar.addEventListener("characteristicvaluechanged", async function(ev){
                        //console.log(ev);
                        var received = false;
                        if(!(received = $scope.ble.convert(ev))) {return;}

                        //console.log(received);

                        //timeNow = window.performance.now();
                        $scope.ble.timeNow = $.now();
                        //console.log($scope.ble.timeNow);

                        var timeDiff = $scope.ble.timeNow - $scope.ble.timeNotifyLast;
                        //console.log("Time diff: " + timeDiff);
                        //console.log(timeDiff);
                        if($scope.ble.lastTS){
                          var difference = ev.timeStamp - $scope.ble.lastTS;
                          //console.log("Time difference: " + difference);
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
                           //$scope.status = "New song successfully created.";
                           //createUpdateTools.setAlerts([{type: 'success', title: 'Successful!', msg: $scope.status}]);
                           //$location.path("/songsOverview");
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
