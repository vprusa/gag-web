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
                info : "Watch 3D model of hands",
                data : []
              };

              $scope.selectedGestureList = {
                display: -1,
                data : []
              };

              $scope.currentGesture = {
                data: {
                /*
                  rq: new THREE.Quaternion(),
                  rqt: new THREE.Quaternion(),
                  rqi: new THREE.Quaternion(),
                  rqm: new THREE.Quaternion(),
                  rqr: new THREE.Quaternion(),
                  rqi: new THREE.Quaternion(),

                  lq: new THREE.Quaternion(),
                  lqt: new THREE.Quaternion(),
                  lqi: new THREE.Quaternion(),
                  lqm: new THREE.Quaternion(),
                  lqr: new THREE.Quaternion(),
                  lqi: new THREE.Quaternion()
                  */
                    rq: null,
                    rqt: null,
                    rqi: null,
                    rqm: null,
                    rqr: null,
                    rqi: null,

                    lq: null,
                    lqt: null,
                    lqi: null,
                    lqm: null,
                    lqr: null,
                    lqi: null

                }
               /* data: {
                  rx: 0.0,ry: 0.0,rz: 0.0,
                  rtx: 0.0,rty: 0.0,rtz: 0.0,
                  rix: 0.0,riy: 0.0,riz: 0.0,
                  rmx: 0.0,rmy: 0.0,rmz: 0.0,
                  rrx: 0.0,rry: 0.0,rrz: 0.0,
                  rlx: 0.0,rly: 0.0,rlz: 0.0,

                  lx: 0.0,ly: 0.0,lz: 0.0,
                  ltx: 0.0,lty: 0.0,ltz: 0.0,
                  lix: 0.0,liy: 0.0,liz: 0.0,
                  lmx: 0.0,lmy: 0.0,lmz: 0.0,
                  lrx: 0.0,lry: 0.0,lrz: 0.0,
                  llx: 0.0,lly: 0.0,llz: 0.0
                }*/
              };

              $scope.quatToAng = function(dl){
                var quaternion = new THREE.Quaternion(parseFloat(dl.qX),
                parseFloat(dl.qY), parseFloat(dl.qZ), parseFloat(dl.qA));
                //quaternion.setFromAxisAngle( new THREE.Vector3( 1, 0, 0 ), Math.PI / 2 );
                var vector = new THREE.Vector3(1,1,1);
                vector.applyQuaternion( quaternion );
                return vector;
              }

              $scope.updateVisFromDataLine = function(){
                //console.log( $scope.selectedGestureDetail.data[0]);
                var dl = $scope.selectedGestureDetail.data[0];
                //console.log(dl.p);
                switch(dl.p) {
                    case "THUMB":
                        //var ar = $scope.quatToAng(dl);
//                        $scope.currentGesture.data.rtx = ar.x;
//                        $scope.currentGesture.data.rty = ar.y;
//                        $scope.currentGesture.data.rtz = ar.z;
                        var ar = new THREE.Quaternion(parseFloat(dl.qX),parseFloat(dl.qY), parseFloat(dl.qZ), parseFloat(dl.qA));
                        $scope.currentGesture.data.rqt = ar;
                        $scope.updateVisualization();
                        break;
                    case "INDEX":
                        //var ar = $scope.quatToAng(dl);
//                        $scope.currentGesture.data.rix = ar.x;
//                        $scope.currentGesture.data.riy = ar.y;
//                        $scope.currentGesture.data.riz = ar.z;
                        var ar = new THREE.Quaternion(parseFloat(dl.qX),parseFloat(dl.qY), parseFloat(dl.qZ), parseFloat(dl.qA));
                        $scope.currentGesture.data.rqi = ar;
                        $scope.updateVisualization();
                        break;
                   // TODO move wrist case up
                   /*case "WRIST":
                        var ar = $scope.quatToAng(dl);
                        $scope.currentGesture.data.rx = ar.x;
                        $scope.currentGesture.data.ry = ar.y;
                        $scope.currentGesture.data.rz = ar.z;
                        break;*/
                   case "MIDDLE":
                        //var ar = $scope.quatToAng(dl);
//                        $scope.currentGesture.data.rmx = ar.x;
//                        $scope.currentGesture.data.rmy = ar.y;
                        //$scope.currentGesture.data.rmz = ar.z;
                        var ar = new THREE.Quaternion(parseFloat(dl.qX),parseFloat(dl.qY), parseFloat(dl.qZ), parseFloat(dl.qA));
                        $scope.currentGesture.data.rqm = ar;
                        $scope.updateVisualization();
                        break;
                    case "RING":
                        //var ar = $scope.quatToAng(dl);
                        //$scope.currentGesture.data.rrx = ar.x;
                        //$scope.currentGesture.data.rry = ar.y;
                        //$scope.currentGesture.data.rrz = ar.z;
                        var ar = new THREE.Quaternion(parseFloat(dl.qX),parseFloat(dl.qY), parseFloat(dl.qZ), parseFloat(dl.qA));
                        $scope.currentGesture.data.rqr = ar;
                        $scope.updateVisualization();
                        break;
                    case "LITTLE":
                        //var ar = $scope.quatToAng(dl);
                        //$scope.currentGesture.data.rlx = ar.x;
                        //$scope.currentGesture.data.rly = ar.y;
                        //$scope.currentGesture.data.rlz = ar.z;
                        var ar = new THREE.Quaternion(parseFloat(dl.qX),parseFloat(dl.qY), parseFloat(dl.qZ), parseFloat(dl.qA));
                        $scope.currentGesture.data.rql = ar;
                        $scope.updateVisualization();
                        break;
                    default:
                }
              }

              /*
              handVisualization.scene.updateAngles(
                0,0,0,
                0,0,0,
                0,0,0,
                0,0,0,
                0,0,0,
                0,0,0,

                0,0,0,
                0,0,0,
                0,0,0,
                0,0,0,
                0,0,0,
                0,0,0
              );
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
              //console.log(data);

              $scope.updateVisualization = function(){
                console.log("updateVisualization");
                var data = JSON.parse(JSON.stringify($scope.currentGesture.data));
               //a $scope.currentGesture.data.rmx += 0.01;
                console.log($scope.currentGesture.data);
                $scope.updateVisualizationAsync(data);
              };

              $scope.updateVisualizationAsync = async function(data){
                //if(data.rq!= null && data.rqt!= null &&  data.rqi!= null &&  data.rqm!= null &&  data.rqr!= null &&  data.rql!= null &&
                //                   data.lq!= null && data.lqt!= null &&  data.lqi!= null &&  data.lqm!= null &&  data.lqr!= null &&  data.lql !=null){
                      if(data.rq == null){
                        data.rq = new THREE.Quaternion();
                        }
                      if(data.rqt == null){
                        data.rqt = new THREE.Quaternion();
                        }
                      if(data.rqi == null){
                        data.rqi = new THREE.Quaternion();
                        }
                      if(data.rqm == null){
                        data.rqm = new THREE.Quaternion();
                        }
                      if(data.rqr == null){
                        data.rqr = new THREE.Quaternion();
                        }
                      if(data.rql == null){
                        data.rql = new THREE.Quaternion();
                        }

                      if(data.lq == null){
                        data.lq = new THREE.Quaternion();
                        }
                      if(data.lqt == null){
                        data.lqt = new THREE.Quaternion();
                        }
                      if(data.lqi == null){
                        data.lqi = new THREE.Quaternion();
                        }
                      if(data.lqm == null){
                        data.lqm = new THREE.Quaternion();
                        }
                      if(data.lqr== null){
                        data.lqr= new THREE.Quaternion();
                        }
                      if(data.lql== null){
                        data.lql= new THREE.Quaternion();
                        }
                    console.log("updateVisualizationAsync")
                    console.log(data)
                    handVisualization.scene.updateAngles(

                      data.rq._x, data.rq._y, data.rq._z, data.rq._w,
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

                      //data.rq,data.rqt, data.rqi, data.rqm, data.rqr, data.rql,
                      //data.lq,data.lqt, data.lqi, data.lqm, data.lqr, data.lql
           /*           data.rx,data.ry,data.rz,
                      //Math.PI,Math.PI,Math.PI,
                      //0,0,0,
                      data.rtx,data.rty,data.rtz,
                      data.rix,data.riy,data.riz,
                      data.rmx,data.rmy,data.rmz,
                      data.rrx,data.rry,data.rrz,
                      data.rlx,data.rly,data.rlz,



                      data.lx,data.ly,data.lz,
                      data.ltx,data.lty,data.ltz,
                      data.lix,data.liy,data.liz,
                      data.lmx,data.lmy,data.lmz,
                      data.lrx,data.lry,data.lrz,
                      data.llx,data.lly,data.llz


                      data.rtx+data.rx,data.rty+data.ry,data.rtz+data.rz,
                      data.rix+data.rx,data.riy+data.ry,data.riz+data.rz,
                      data.rmx+data.rx,data.rmy+data.ry,data.rmz+data.rz,
                      data.rrx+data.rx,data.rry+data.ry,data.rrz+data.rz,
                      data.rlx+data.rx,data.rly+data.ry,data.rlz+data.rz,
    */
    /*
                      data.rtx-data.rx,data.rty-data.ry,data.rtz-data.rz,
                      data.rix-data.rx,data.riy-data.ry,data.riz-data.rz,
                      data.rmx-data.rx,data.rmy-data.ry,data.rmz-data.rz,
                      data.rrx-data.rx,data.rry-data.ry,data.rrz-data.rz,
                      data.rlx-data.rx,data.rly-data.ry,data.rlz-data.rz,

                      */
                    );
                    /*
                } else {
                  data.rq = new THREE.Quaternion();
                  data.rqt = new THREE.Quaternion();
                  data.rqi = new THREE.Quaternion();
                  data.rqm = new THREE.Quaternion();
                  data.rqr = new THREE.Quaternion();
                  data.rql = new THREE.Quaternion();

                  data.lq = new THREE.Quaternion();
                  data.lqt = new THREE.Quaternion();
                  data.lqi = new THREE.Quaternion();
                  data.lqm = new THREE.Quaternion();
                  data.lqr= new THREE.Quaternion();
                  data.lql= new THREE.Quaternion();
                }
                */
                //await handVisualization.scene.renderAll();
              }

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
              }

              $scope.onMessage = function(evt) {
                  var dataLine = JSON.parse(evt.data);
                  $scope.selectedGestureDetail.data = [dataLine];
                  $scope.updateVisFromDataLine();
                  $scope.$apply();
              };

              $scope.ws.playSelectedGesture = function() {
                  if($scope.selectedGestureDetail.selectedGesture)
                  $scope.ws.init();
                  $scope.ws.setOnMessage($scope.onMessage);
                  console.log($scope.selectedGestureDetail.selectedGesture);
                  $scope.ws.websocketSession.send('{"action":"replay", "gestureId": '
                    + $scope.selectedGestureDetail.selectedGesture + '}');
              };


            } ]);
