'use strict';

/**
 * @author Vojtěch Průša (prusa.vojtech@gmail.com)
 */
console.log("init app vis tools");

function formatTimestamp(timestamp) {
  const date = new Date(timestamp);
  return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ` +
      `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}:${date.getSeconds().toString().padStart(2, '0')}.${date.getMilliseconds().toString().padStart(3, '0')}`;
}


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
    currentTimeHuman: 0,
    currentDLId: 0,
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

  vis.visualizationOffsets = {};
  window.vis = vis;
  // TODO use global variables for each hand and sensor
  ["left", "right"].forEach(hand => {
    ["wrist", "thumb", "index", "middle", "ring", "little"].forEach(finger => {
      ["x", "y", "z"].forEach(axis => {
        let key = `${hand}-${finger}-${axis}-num`;
        vis.visualizationOffsets[key] = 0; // Default to 0
      });
    });
  });

  vis.resetProgressBar = function () {
    vis.currentGesture.currentPercentage = 0;
    vis.currentGesture.startTime = 0;
    vis.currentGesture.currentTime = 0;
    // vis.currentGesture.currentTimeHuman = 0;
  };
  vis.updateVisFromDataLineWristDone = false;
  // TODO move parseFloat somewhere else, e.g. inside THREE.Quaternion ?
  vis.updateVisFromDataLine = function (dl) {
    vis.updateVisFromDataLineWristDone = false;
    vis.currentGesture.currentTime = dl.t;
    vis.currentGesture.currentTimeHuman = formatTimestamp(dl.t); // new Date(dl.t).toLocaleTimeString()
    vis.currentGesture.currentDLId = dl.id;

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
      case "WRIST":{
        // TODO fix, should be dealt in backend response...
      // case null:{
        let ar = new THREE.Quaternion(parseFloat(dl.qX), parseFloat(dl.qY), parseFloat(dl.qZ), parseFloat(dl.qA));
        vis.currentGesture.data.rq = ar;
        vis.updateVisualization();
        vis.updateVisFromDataLineWristDone = true;
      }break;
      case "MIDDLE":{
        let ar = new THREE.Quaternion(parseFloat(dl.qX), parseFloat(dl.qY), parseFloat(dl.qZ), parseFloat(dl.qA));
        vis.currentGesture.data.rqm = ar;
        vis.updateVisualization();
      }break;
      case "RING":{
        let ar = new THREE.Quaternion(parseFloat(dl.qX), parseFloat(dl.qY), parseFloat(dl.qZ), parseFloat(dl.qA));
        vis.currentGesture.data.rqr = ar;
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

  vis.updateVisualization = function () {
    var data = JSON.parse(JSON.stringify(vis.currentGesture.data));
    vis.currentGesture.progressPercentage = (vis.currentGesture.currentTime - vis.currentGesture.startTime)/
        (vis.currentGesture.endTime - vis.currentGesture.startTime);
    // console.log(vis.currentGesture.progressPercentage);
    vis.updateVisualizationAsync(data);
  };

  console.log("Init logs");
  vis.updateVisualizationAsync = async function (data) {
    if (!window.leftHandSkeleton || !window.rightHandSkeleton) {
      console.error("Hand skeletons are not initialized.");
      return;
    }

    const useLittle = false;
    // const useLittle = true;

    if (useLittle) {

      // Update wrist rotations
      const leftWristQuaternion = new THREE.Quaternion(data.lq._x, data.lq._y, data.lq._z, data.lq._w);
      // const rightWristQuaternion = new THREE.Quaternion(data.rql._x, data.rql._y, data.rql._z, data.rql._w);
      const rightWristQuaternion = new THREE.Quaternion(data.rq._x, data.rq._y, data.rq._z, data.rq._w);

      // Read wrist-x input and convert degrees to radians
      const wristXOffset = parseFloat(vis.visualizationOffsets["right-wrist-x-num"]) * (Math.PI / 180);
      const wristYOffset = parseFloat(vis.visualizationOffsets["right-wrist-y-num"]) * (Math.PI / 180);
      const wristZOffset = parseFloat(vis.visualizationOffsets["right-wrist-z-num"]) * (Math.PI / 180);
      const wristOffsetQuaternion = new THREE.Quaternion().setFromEuler(new THREE.Euler(wristXOffset, wristYOffset, wristZOffset));

      // Define correction quaternion to rotate -90° around X-axis
      const correctionQuaternion = new THREE.Quaternion();
      const correctionQuaternion2 = new THREE.Quaternion();
      correctionQuaternion.setFromAxisAngle(new THREE.Vector3(1, 0, 0), -Math.PI );
      correctionQuaternion2.setFromAxisAngle(new THREE.Vector3(0, 1, 0), Math.PI);
      // rightWristQuaternion.premultiply(correctionQuaternion);

      // Correct right wrist orientation
      rightWristQuaternion.premultiply(wristOffsetQuaternion);
      // rightWristQuaternion.invert();
      window.rightHandSkeleton.wristJoint.setRotationFromQuaternion(rightWristQuaternion.invert());

      window.leftHandSkeleton.wristJoint.setRotationFromQuaternion(leftWristQuaternion);

      // Update fingers relative to wrist
      const fingers = ["thumb", "index", "middle", "ring", "little"];
      const leftFingerData = [data.lqt, data.lqi, data.lqm, data.lqr, data.lql];
      const rightFingerData = [data.rqt, data.rqi, data.rqm, data.rqr, data.rql];

      fingers.forEach((finger, index) => {
        if (window.leftHandSkeleton.wristJoint.fingers[finger]) {
          // todo use invert()
          const leftQuat = new THREE.Quaternion(leftFingerData[index]._x, leftFingerData[index]._y, leftFingerData[index]._z, leftFingerData[index]._w);
          window.leftHandSkeleton.wristJoint.fingers[finger].baseJoint.setRotationFromQuaternion(leftQuat);
        }
        if (finger == "little") {
          // skip little finger that acts as wrist data
        } else {
          if (window.rightHandSkeleton.wristJoint.fingers[finger]) {
            // console.log(finger);
            const fingerXOffset = parseFloat(vis.visualizationOffsets["right-" + finger + "-x-num"]) * (Math.PI / 180);
            const fingerYOffset = parseFloat(vis.visualizationOffsets["right-" + finger + "-y-num"]) * (Math.PI / 180);
            const fingerZOffset = parseFloat(vis.visualizationOffsets["right-" + finger + "-z-num"]) * (Math.PI / 180);
            const offsetQuaternion = new THREE.Quaternion().setFromEuler(new THREE.Euler(fingerXOffset, fingerYOffset, fingerZOffset));
            const rightQuat = new THREE.Quaternion(rightFingerData[index]._x, rightFingerData[index]._y, rightFingerData[index]._z, rightFingerData[index]._w);
            // window.rightHandSkeleton.wristJoint.fingers[finger].tipJoint.quaternion
            //     .multiplyQuaternions(rightWristQuaternion.clone().invert(), rightQuat)
            //     .multiply(offsetQuaternion).invert();

            var wqback = rightWristQuaternion.clone();
            window.rightHandSkeleton.wristJoint.fingers[finger].tipJoint.quaternion
                .multiply(wristOffsetQuaternion.invert())
                // .multiplyQuaternions(wqback, rightQuat.invert())
                .multiplyQuaternions(wqback.invert(), rightQuat.invert())
                .multiply(offsetQuaternion);

          // .multiplyQuaternions(wqback, rightQuat.invert());
            // .multiplyQuaternions(rightWristQuaternion.clone().invert(), rightQuat)
                // .multiplyQuaternions(rightWristQuaternion.clone().invert(), rightQuat);
                // .multiplyQuaternions(wqback, rightQuat);
                // .multiplyQuaternions(wqback.invert(), rightQuat.invert());
          // .multiplyQuaternions(wqback.invert(), rightQuat.invert());
          //       .multiplyQuaternions(offsetQuaternion.invert(), rightQuat);
                // .multiplyQuaternions(offsetQuaternion, rightQuat);
            // .multiply(offsetQuaternion).invert();

          }
        }

      });
    } else {

      function getRotatedQuaternion(where) {
        const axis = new THREE.Vector3(1, 0, 0); // Rotate around the Y-axis
        const angle = THREE.MathUtils.degToRad(180); // Convert degrees to radians
        const quaternion = new THREE.Quaternion();
        quaternion.setFromAxisAngle(axis, angle);
        // console.log("using default wrist quaternion offset: " + where);
        return quaternion;
      }

      // Update wrist rotations

      const leftWristQuaternion = (data.lq && data.lq._x !== undefined)
          ? new THREE.Quaternion(data.lq._x, data.lq._y, data.lq._z, data.lq._w)
          : getRotatedQuaternion("left");

      const rightWristQuaternion = (data.rq && data.rq._x !== undefined
          // quickfix missing wrist data, needs cleaning, unused duplicates of getRotatedQuaternion
      && (data.rq._x != 0 && data.rq._z != 0 && data.rq._y != 0 && data.rq._w != 1 ))
          ? new THREE.Quaternion(data.rq._x, data.rq._y, data.rq._z, data.rq._w)
          : getRotatedQuaternion("right");
      // Read wrist-x input and convert degrees to radians
      // const wristXOffset = parseFloat(document.getElementById("right-wrist-x-num").value) * (Math.PI / 180);
      const wristXOffset = parseFloat(vis.visualizationOffsets["right-wrist-x-num"]) * (Math.PI / 180);
      const wristYOffset = parseFloat(vis.visualizationOffsets["right-wrist-y-num"]) * (Math.PI / 180);
      const wristZOffset = parseFloat(vis.visualizationOffsets["right-wrist-z-num"]) * (Math.PI / 180);
      const offsetQuaternion = new THREE.Quaternion().setFromEuler(new THREE.Euler(wristXOffset, wristYOffset, wristZOffset));

      // Correct right wrist orientation
      rightWristQuaternion.multiply(offsetQuaternion);
      window.rightHandSkeleton.wristJoint.setRotationFromQuaternion(rightWristQuaternion.invert());

      window.leftHandSkeleton.wristJoint.setRotationFromQuaternion(leftWristQuaternion);

      // Update fingers relative to wrist
      const fingers = ["thumb", "index", "middle", "ring", "little"];
      const leftFingerData = [data.lqt, data.lqi, data.lqm, data.lqr, data.lql];
      const rightFingerData = [data.rqt, data.rqi, data.rqm, data.rqr, data.rql];

      fingers.forEach((finger, index) => {
        if (window.leftHandSkeleton.wristJoint.fingers[finger]) {
          // todo use invert()
          const leftQuat = new THREE.Quaternion(leftFingerData[index]._x, leftFingerData[index]._y, leftFingerData[index]._z, leftFingerData[index]._w);
          window.leftHandSkeleton.wristJoint.fingers[finger].baseJoint.setRotationFromQuaternion(leftQuat);
        }
          if (window.rightHandSkeleton.wristJoint.fingers[finger]) {
            // console.log(finger);
            const fingerXOffset = parseFloat(vis.visualizationOffsets["right-" + finger + "-x-num"]) * (Math.PI / 180);
            const fingerYOffset = parseFloat(vis.visualizationOffsets["right-" + finger + "-y-num"]) * (Math.PI / 180);
            const fingerZOffset = parseFloat(vis.visualizationOffsets["right-" + finger + "-z-num"]) * (Math.PI / 180);
            const offsetQuaternion = new THREE.Quaternion().setFromEuler(new THREE.Euler(fingerXOffset, fingerYOffset, fingerZOffset));
            const rightQuat = new THREE.Quaternion(rightFingerData[index]._x, rightFingerData[index]._y, rightFingerData[index]._z, rightFingerData[index]._w);

            var wqback = rightWristQuaternion.clone();
            window.rightHandSkeleton.wristJoint.fingers[finger].tipJoint.quaternion
                .multiplyQuaternions(wqback.invert(), rightQuat.invert())
                .multiply(offsetQuaternion);
        }

      });

    }

  };


  vis.forceApplyOffsets = function () {
    const parts = ['WRIST', 'THUMB', 'INDEX', 'MIDDLE', 'RING', 'LITTLE'];
    const map = {
      WRIST: 'rq',
      THUMB: 'rqt',
      INDEX: 'rqi',
      MIDDLE: 'rqm',
      RING: 'rqr',
      LITTLE: 'rql'
    };

    const now = Date.now();

    parts.forEach((part, index) => {
      const key = map[part];
      const q = vis.currentGesture.data[key];

      if (!q || q._x === undefined) {
        console.warn(`No quaternion data for ${part}`);
        return;
      }

      const mockDataLine = {
        p: part,
        id: vis.currentGesture.currentDLId + index + 1,
        t: now,
        qX: q._x,
        qY: q._y,
        qZ: q._z,
        qA: q._w
      };

      vis.updateVisFromDataLine(mockDataLine);
    });

  };

  return vis;
});
