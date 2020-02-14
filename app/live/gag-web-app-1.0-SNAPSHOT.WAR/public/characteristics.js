//https://googlechrome.github.io/samples/web-bluetooth/get-characteristics.html?service=6e400001-b5a3-f393-e0a9-e50e24dcca9e&characteristic_read=6e400002-b5a3-f393-e0a9-e50e24dcca9e

var bluetoothDevice;
var bluetoothDeviceWriteChar;
var bluetoothDeviceNotifyChar;

var timeNow, timeWriteLast, timeNotifyLast;

function log(msg){
console.log(msg);}

function str2ab(str) {
  var buf = new ArrayBuffer(str.length*2); // 2 bytes for each char
  var bufView = new Uint8Array(buf);
  for (var i=0, strLen=str.length; i < strLen; i++) {
    bufView[i] = str.charCodeAt(i);
  }
  return buf;
}

function ab2str(buf) {
  return String.fromCharCode.apply(null, new Uint8Array(buf));
}

function sendValue() {
    console.log("sendval");
  if(bluetoothDeviceWriteChar) {
    var toSend = $("#toSend").val();
    //console.log(toSend);
    //console.log(bluetoothDeviceWriteChar);
    var toSendAB = str2ab(toSend);
    bluetoothDeviceWriteChar.writeValue(toSendAB);
  }
}

function startPerfTest() {
  console.log("startPerfTest");
  // TODO send chars/strings and calculate avg wait time
}

function showReceivedValue(value, timeNow, timeDiff) {
  //log(value);
  $('#content table tr:last').after(
    "<tr><td>" + timeNow + "</td><td>" + timeDiff + "</td><td>" + value + "</td></tr>"
  );
}

function connect() {
  exponentialBackoff(5 /* max retries */, 2 /* seconds delay */,
    function toTry() {
      time('Connecting to Bluetooth Device... ');
      return bluetoothDevice.gatt.connect();
    },
    function success() {
      log('> Bluetooth Device connected.');
    },
    function fail() {
      time('Failed to reconnect.');
    });
}

function onDisconnected() {
  log('> Bluetooth Device disconnected');
  connect();
}

function onReconnectButtonrClick(){
  connect();
}
function onButtonClick() {
  let serviceUuid = document.querySelector('#service').value;
  if (serviceUuid.startsWith('0x')) {
    serviceUuid = parseInt(serviceUuid);
  }

  let name = document.querySelector('#name').value;

  let characteristicWriteUuid = document.querySelector('#characteristic-write').value;
  if (characteristicWriteUuid.startsWith('0x')) {
    characteristicWriteUuid = parseInt(characteristicWriteUuid);
  }
  let characteristicNotifyUuid = document.querySelector('#characteristic-notify').value;
  if (characteristicNotifyUuid.startsWith('0x')) {
    characteristicNotifyUuid = parseInt(characteristicNotifyUuid);
  }

  log('Requesting Bluetooth Device...');
  function resolve(){
    if (bluetoothDevice) {
      return bluetoothDevice;
    }
  }

  let promise = new Promise(
    function(resolve, reject) {
      if(bluetoothDevice) {
        connect();
        // TODO fix return values and reconnect ...
        console.log(bluetoothDevice);
        resolve(bluetoothDevice.gatt);
      } else {
        resolve(
          navigator.bluetooth.requestDevice({ filters: [{services: [serviceUuid], name: [name]}]}).then(device => {
            log('Connecting to GATT Server...');
            bluetoothDevice = device;
            bluetoothDevice.addEventListener('gattserverdisconnected', onDisconnected);
            return device.gatt.connect();
          })
        );
      }
    }
  )

  promise.then(server => {
    log('Getting Service...');
    return server.getPrimaryService(serviceUuid);
  })
  .then(service => {
    log('Getting Characteristics...');
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
    log('> Characteristics: ' +
      characteristics.map(c => c[0].uuid + " ("+(c[0].properties.write === true ? "WRITE" : (c[0].properties.notify === true ? "NOTIFY":"?"))+")").join('\n' + ' '.repeat(19)));
      console.log(characteristics);
      bluetoothDeviceWriteChar = characteristics[0][0];
      bluetoothDeviceNotifyChar = characteristics[1][0];
      //timeNow = window.performance.now();
      timeNow = $.now();
      console.log("Time timeNow: " + timeNow);

      bluetoothDeviceNotifyChar.addEventListener("characteristicvaluechanged",async function(ev){
        console.log(ev);
        var received = ab2str(ev.currentTarget.value.buffer);
        if(received == -1 ) {return;}

        console.log(received);
        //timeNow = window.performance.now();
        timeNow = $.now();
        console.log(timeNow);

        var timeDiff = timeNow - timeNotifyLast;
        console.log("Time diff: " + timeDiff);
        console.log(timeDiff);
        if(lastTS){
          var difference = ev.timeStamp - lastTS;
          console.log("Time difference: " + difference);

        }
        showReceivedValue(received, timeNow, timeDiff);
        lastTS = ev.timeStamp;
        timeNotifyLast = timeNow;
      });
      bluetoothDeviceNotifyChar.startNotifications();
  })
  .catch(error => {
    log('Argh! ' + error);
  });
}
var lastTS;


/* Utils */

// This function keeps calling "toTry" until promise resolves or has
// retried "max" number of times. First retry has a delay of "delay" seconds.
// "success" is called upon success.
function exponentialBackoff(max, delay, toTry, success, fail) {
  toTry().then(result => success(result))
  .catch(_ => {
    if (max === 0) {
      return fail();
    }
    time('Retrying in ' + delay + 's... (' + max + ' tries left)');
    setTimeout(function() {
      exponentialBackoff(--max, delay * 2, toTry, success, fail);
    }, delay * 1000);
  });
}

function time(text) {
  log('[' + new Date().toJSON().substr(11, 8) + '] ' + text);
}
