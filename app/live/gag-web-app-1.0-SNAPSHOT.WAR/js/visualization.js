'use strict';

angular.module('app').factory('VisTools', function() {
    // visualization
    let vis = {
        currentGesture: {
            data: {}
        }
    };
    vis.currentGesture = {
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

      vis.updateVisFromDataLine = function(dl){
        console.log("updateVisFromDataLine");
        console.log(dl);
        switch(dl.p) {
            case "THUMB":
                var ar = new THREE.Quaternion(parseFloat(dl.qX),parseFloat(dl.qY), parseFloat(dl.qZ), parseFloat(dl.qA));
                vis.currentGesture.data.rqt = ar;
                vis.updateVisualization();
                break;
            case "INDEX":
                var ar = new THREE.Quaternion(parseFloat(dl.qX),parseFloat(dl.qY), parseFloat(dl.qZ), parseFloat(dl.qA));
                vis.currentGesture.data.rqi = ar;
                vis.updateVisualization();
                break;
           // TODO move wrist case up
           case "WRIST":
                var ar = new THREE.Quaternion(parseFloat(dl.qX),parseFloat(dl.qY), parseFloat(dl.qZ), parseFloat(dl.qA));
                vis.currentGesture.data.rq = ar;
                vis.updateVisualization();
                break;
           case "MIDDLE":
                var ar = new THREE.Quaternion(parseFloat(dl.qX),parseFloat(dl.qY), parseFloat(dl.qZ), parseFloat(dl.qA));
                vis.currentGesture.data.rqm = ar;
                vis.updateVisualization();
                break;
            case "RING":
                var ar = new THREE.Quaternion(parseFloat(dl.qX),parseFloat(dl.qY), parseFloat(dl.qZ), parseFloat(dl.qA));
                vis.currentGesture.data.rqr = ar;
                vis.updateVisualization();
                break;
            case "LITTLE":
                var ar = new THREE.Quaternion(parseFloat(dl.qX),parseFloat(dl.qY), parseFloat(dl.qZ), parseFloat(dl.qA));
                vis.currentGesture.data.rql = ar;
                vis.updateVisualization();
                break;
            default:
        }
      }

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

      vis.updateVisualization = function(){
        var data = JSON.parse(JSON.stringify(vis.currentGesture.data));
        vis.updateVisualizationAsync(data);
      };

      vis.updateVisualizationAsync = async function(data){
            //console.log("updateVisualizationAsync")
            //console.log(data)
            //data.rq.normalize();
            //data.rq._x *= 10.0;
            //data.rq._y *= 10.0;
            //data.rq._z *= 10.0;
            //data.rq._w *= 10.0;
            console.log(data.rqm);
            handVisualization.scene.updateAngles(
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
            );
      }

    return vis;
} );
