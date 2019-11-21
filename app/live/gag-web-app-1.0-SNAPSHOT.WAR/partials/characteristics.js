/**
 * This is a test js file for
 *
 *
 *
 *
 */
//https://googlechrome.github.io/samples/web-bluetooth/get-characteristics.html?service=6e400001-b5a3-f393-e0a9-e50e24dcca9e&characteristic_read=6e400002-b5a3-f393-e0a9-e50e24dcca9e
var bluetoothDevice;
var bluetoothDeviceWriteChar;
var bluetoothDeviceNotifyChar;

var timeNow, timeWriteLast, timeNotifyLast;

function str2ab(str) {
  var buf = new ArrayBuffer(str.length * 2); // 2 bytes for each char
  var bufView = new Uint8Array(buf);
  for (var i = 0, strLen = str.length; i < strLen; i++) {
    bufView[i] = str.charCodeAt(i);
  }
  return buf;
}

function ab2str(buf) {
  return String.fromCharCode.apply(null, new Uint8Array(buf));
}

function sendValue() {
  if (bluetoothDeviceWriteChar) {
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

function onReconnectButtonClick() {
  connect();
}

function connectBLE() {

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
      setTimeout(function () {
        exponentialBackoff(--max, delay * 2, toTry, success, fail);
      }, delay * 1000);
    });
}

function time(text) {
  log('[' + new Date().toJSON().substr(11, 8) + '] ' + text);
}
