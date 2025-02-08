'use strict';

/**
 * @author Vojtěch Průša (prusa.vojtech@gmail.com)
 */
console.log("init app vis tools");
angular.module('app').factory('VisTools', function () {
  // visualization
  let vis = {
    currentGesture: {
      data: {}
    }
  };

  vis.currentGesture = {
    startTime: 0,
    currentTime: 0,
    endTime: 0,
    progressPercentage: 0,
    data: {
      rq: new THREE.Quaternion(),
      rqt: new THREE.Quaternion(),
      rqi: new THREE.Quaternion(),
      rqm: new THREE.Quaternion(),
      rqr: new THREE.Quaternion(),
      rql: new THREE.Quaternion(),

      lq: new THREE.Quaternion(),
      lqt: new THREE.Quaternion(),
      lqi: new THREE.Quaternion(),
      lqm: new THREE.Quaternion(),
      lqr: new THREE.Quaternion(),
      lql: new THREE.Quaternion()
    }
  };

  vis.resetProgressBar = function () {
    vis.currentGesture.currentPercentage = 0;
    vis.currentGesture.startTime = 0;
    vis.currentGesture.currentTime = 0;
  };

  // TODO move parseFloat somewhere else, e.g. inside THREE.Quaternion ?
  vis.updateVisFromDataLine = function (dl) {
    //console.log("updateVisFromDataLine");
    //console.log(dl);
    vis.currentGesture.currentTime = dl.t;
    switch (dl.p) {
      case "THUMB":{
        let ar = new THREE.Quaternion(parseFloat(dl.qX), parseFloat(dl.qY), parseFloat(dl.qZ), parseFloat(dl.qA));
        vis.currentGesture.data.rqt = ar;
        vis.updateVisualization();
      }break;
      case "INDEX": {
        let ar = new THREE.Quaternion(parseFloat(dl.qX), parseFloat(dl.qY), parseFloat(dl.qZ), parseFloat(dl.qA));
        vis.currentGesture.data.rqi = ar;
        vis.updateVisualization();
      }break;
      // TODO move wrist case up
      case "WRIST":
      // TODO fix, should be dealt in backend response... 
      case null:{
        let ar = new THREE.Quaternion(parseFloat(dl.qX), parseFloat(dl.qY), parseFloat(dl.qZ), parseFloat(dl.qA));
        vis.currentGesture.data.rq = ar;
        vis.updateVisualization();
      }break;
      case "MIDDLE":{
        let ar = new THREE.Quaternion(parseFloat(dl.qX), parseFloat(dl.qY), parseFloat(dl.qZ), parseFloat(dl.qA));
        vis.currentGesture.data.rqm = ar;
        // console.log("MIDDLE: data.rql");
        // console.log(vis.currentGesture.data.rql);

        // console.log("data.rqr");
        // console.log(vis.currentGesture.data.rqr);
        vis.updateVisualization();
      }break;
      case "RING":{
        let ar = new THREE.Quaternion(parseFloat(dl.qX), parseFloat(dl.qY), parseFloat(dl.qZ), parseFloat(dl.qA));

        vis.currentGesture.data.rqr = ar;
        // console.log("RING: data.rql");
        // console.log(vis.currentGesture.data.rql);

        // console.log("data.rqr");
        // console.log(vis.currentGesture.data.rqr);
        vis.updateVisualization();
      }break;
      case "LITTLE":{
        let ar = new THREE.Quaternion(parseFloat(dl.qX), parseFloat(dl.qY), parseFloat(dl.qZ), parseFloat(dl.qA));
        vis.currentGesture.data.rql = ar;
        vis.updateVisualization();
      }break;
      default:
    }
  };

  /*
  handVisualization.scene.renderAll();
  <th>ID</th>
  <th>Time</th>
  <th>Position</th>
  <th>quatA</th>
  <th>quatX</th>
  <th>quatY</th>
  <th>quatZ</th>
  <th>accX</th>
  <th>accY</th>
  <th>accZ</th>
  <th>magX</th>
  <th>magY</th>
  <th>magZ</th>
  */

  vis.updateVisualization = function () {
    var data = JSON.parse(JSON.stringify(vis.currentGesture.data));
    vis.currentGesture.progressPercentage = (vis.currentGesture.currentTime - vis.currentGesture.startTime)/
     (vis.currentGesture.endTime - vis.currentGesture.startTime);
    // console.log(vis.currentGesture.progressPercentage);
    vis.updateVisualizationAsync(data);
  };

  console.log("Init logs");
  vis.updateVisualizationAsync = async function (data) {
    if (!window.handSkeleton) {
      console.error("Hand skeleton visualization is not initialized.");
      return;
    }

    // Update wrist rotation using quaternion
    const wristQuaternion = new THREE.Quaternion(data.rq._x, data.rq._y, data.rq._z, data.rq._w);
    window.handSkeleton.rotation.setFromQuaternion(wristQuaternion);

    // Update each finger's second joint rotation
    const fingers = {
      thumb: data.rqt,
      index: data.rqi,
      middle: data.rqm,
      ring: data.rqr,
      little: data.rql
    };

    Object.keys(fingers).forEach(finger => {
      if (window.handSkeleton.fingers[finger]) {
        const quaternion = new THREE.Quaternion(
            fingers[finger]._x,
            fingers[finger]._y,
            fingers[finger]._z,
            fingers[finger]._w
        );
        window.handSkeleton.fingers[finger].tipJoint.setRotationFromQuaternion(quaternion);
      }
    });

    //console.log("updateVisualizationAsync")
    //console.log(data)
    //data.rq.normalize();
    //data.rq._x *= 10.0;
    //data.rq._y *= 10.0;
    //data.rq._z *= 10.0;
    //data.rq._w *= 10.0;
    //console.log(data.rqm);
    // console.log("data.rql");
    // console.log(data.rql);

    // console.log("data.rqr");
    // console.log(data.rqr);
/*    handVisualization.scene.updateAngles(0,
      // TODO make wrist work
      //data.rq._y, -data.rq._x, data.rq._z, data.rq._w,
      data.rq._x, data.rq._y, data.rq._z, data.rq._w,
      //-data.rq._x, data.rq._z, -data.rq._y, data.rq._w,
      //data.rqm._x, data.rqm._y, data.rqm._z, data.rqm._w,
      data.rqt._x, data.rqt._y, data.rqt._z, data.rqt._w,
      data.rqi._x, data.rqi._y, data.rqi._z, data.rqi._w,
      data.rqm._x, data.rqm._y, data.rqm._z, data.rqm._w,
      data.rqr._x, data.rqr._y, data.rqr._z, data.rqr._w,
      data.rql._x, data.rql._y, data.rql._z, data.rql._w,

      data.lq._x, data.lq._y, data.lq._z, data.lq._w,
      data.lqt._x, data.lqt._y, data.lqt._z, data.lqt._w,
      data.lqi._x, data.lqi._y, data.lqi._z, data.lqi._w,
      data.lqm._x, data.lqm._y, data.lqm._z, data.lqm._w,
      data.lqr._x, data.lqr._y, data.lqr._z, data.lqr._w,
      data.lql._x, data.lql._y, data.lql._z, data.lql._w
    );*/
  };

  return vis;
});
