/**
 * @author Vojtěch Průša (prusa.vojtech@gmail.com)
 */

// This file is required by the index.html file and will
// be executed in the renderer process for that window.
// No Node.js APIs are available in this process because
// `nodeIntegration` is turned off. Use `preload.js` to
// selectively enable features needed in the rendering
// process.

const { ipcRenderer } = require('electron');

document
  .querySelector('#moveMouse')
  .addEventListener('click', () => {
    console.log('#moveMouse')
    ipcRenderer.send('click');
  });

function getData2() {
  // ...
}

function moveMouse2(){
  console.log("moveMouse");

  // Speed up the mouse.
  robot.setMouseDelay(2);
  console.log("robot1");

  var twoPI = Math.PI * 2.0;
  var screenSize = robot.getScreenSize();
  console.log("robot2");

  var height = (screenSize.height / 2) - 10;
  var width = screenSize.width;

  for (var x = 0; x < width; x++)
  {
    y = height * Math.sin((twoPI * x) / width) + height;
    robot.moveMouse(x, y);
  }
}

console.log("qwe");
/*
$( document ).ready(function() {
  console.log( "ready!" );
  $("#mouseClick").click(function() {
    console.log( "onclick" );
  });
});
*/
// document.querySelector('#moveMouse').addEventListener('click', () => {
// getData()
// console.log("onClick - moveMouse");
// })
