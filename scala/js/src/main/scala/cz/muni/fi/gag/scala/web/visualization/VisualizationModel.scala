package cz.muni.fi.gag.scala.web.visualization

import cz.muni.fi.gag.web.scala.shared.visualization.{HandVisualization, VisualizationBase}
import cz.muni.fi.gag.scala.web.shared.Log
import cz.muni.fi.gag.web.scala.shared.Hand
import cz.muni.fi.gag.web.scala.shared.common.VisualizationContextT
import cz.muni.fi.gag.web.scala.shared.recognition.Sensor
import cz.muni.fi.gag.web.scala.shared.recognition.Sensor.Sensor
import org.denigma.threejs.{Object3D, PerspectiveCamera, _}
import org.denigma.threejs.extensions.Container3D
import org.denigma.threejs.extensions.controls.{CameraControls, JumpCameraControls}
import org.denigma.threejs.extras.HtmlSprite
import org.scalajs
import org.scalajs.dom.MouseEvent
import org.scalajs.dom.raw.HTMLElement
import scalatags.JsDom.all._

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSName}
import scala.util.Random

class VisualizationScene[GeomType<:Object3D, QuaternionType<:Quaternion](val container: HTMLElement, val width: Double, val height: Double)
  extends Container3D with VisualizationContextT[GeomType, QuaternionType] {

  /*
  * To avoid exception
  * THREE.WebGLRenderer.render: camera is not an instance of THREE.Camera
  * it is necessary to initCamera()
  */
  override def onEnterFrame(): Unit = {
    controls.update()
    if (!camera.isInstanceOf[PerspectiveCamera]){
      var cam = initCamera()
      renderer.render(scene, cam)
    } else {
      renderer.render(scene, camera)
    }
    cssRenderer.render(cssScene, camera)
  }

  override protected def initRenderer() = {
    val vr = super.initRenderer()
    vr.domElement.style.position = "relative"
    vr.domElement.style.display = "inline-block"
    vr
  }

  @JSExport("updateMatrix")
  def updateMatrix(): Unit ={
    scene.updateMatrix()
  }

  @JSExport("renderAll")
  def renderAll(): Any ={
    cleanScene()
    drawAllHands()
    onEnterFrame()
    renderer.render(scene, camera)
  }

  @JSExport("updateAngles")
  def updateAngles(handsPairIndex: Int,
      rx: Float, ry: Float, rz: Float, rw: Float,
      rtx: Float, rty: Float, rtz: Float, rtw: Float,
      rix: Float, riy: Float, riz: Float, riw: Float,
      rmx: Float, rmy: Float, rmz: Float, rmw: Float,
      rrx: Float, rry: Float, rrz: Float, rrw: Float,
      rlx: Float, rly: Float, rlz: Float, rlw: Float,

      lx: Float, ly: Float, lz: Float, lw: Float,
      ltx: Float, lty: Float, ltz: Float, ltw: Float,
      lix: Float, liy: Float,  liz: Float, liw: Float,
      lmx: Float, lmy: Float, lmz: Float, lmw: Float,
      lrx: Float, lry: Float, lrz: Float, lrw: Float,
      llx: Float, lly: Float, llz: Float, llw: Float
    ): Any = {
    var lq = new Quaternion(lx,ly,lz,lw)
    var lqt = new Quaternion(ltx,lty,ltz,ltw)
    var lqi = new Quaternion(lix,liy,liz,liw)
    var lqm = new Quaternion(lmx,lmy,lmz,lmw)
    var lqr = new Quaternion(lrx,lry,lrz,lrw)
    var lql = new Quaternion(llx,lly,llz,llw)

    var rq = new Quaternion(ry,-rx,rz,rw)
    var rqt = new Quaternion(rtx,rty,rtz,rtw)
    var rqi = new Quaternion(rix,riy,riz,riw)
    var rqm = new Quaternion(rmx,rmy,rmz,rmw)
    var rqr = new Quaternion(rrx,rry,rrz,rrw)
    var rql = new Quaternion(rlx,rly,rlz,rlw)

    rqt = rqt.normalize()
    rqi = rqi.normalize()
    rqm = rqm.normalize()
    rqr = rqr.normalize()
    rql = rql.normalize()

    //val rqC = rq.conjugate().inverse()
    //val rqC = rq.clone()
    rq = rq.normalize()
//    var rqC = new Quaternion(-rq.x,-rq.y,-rq.z,rq.w).normalize() //rq.normalize() //.conjugate() //.inverse()
    var rqC = new Quaternion(-rq.x,rq.y,-rq.z,rq.w).normalize() //rq.normalize() //.conjugate() //.inverse()
    rqt = rqt.multiply(rqC)
    rqi = rqi.multiply(rqC)
    rqm = rqm.multiply(rqC)
    rqr = rqr.multiply(rqC)
    rql = rql.multiply(rqC)

    hands(handsPairIndex)(1).thumbVis.rotate(rqt.asInstanceOf[QuaternionType])
    hands(handsPairIndex)(1).indexVis.rotate(rqi.asInstanceOf[QuaternionType])
    hands(handsPairIndex)(1).middleVis.rotate(rqm.asInstanceOf[QuaternionType])
    hands(handsPairIndex)(1).ringVis.rotate(rqr.asInstanceOf[QuaternionType])
    hands(handsPairIndex)(1).littleVis.rotate(rql.asInstanceOf[QuaternionType])
    hands(handsPairIndex)(1).rotate(rq.asInstanceOf[QuaternionType])

    hands(handsPairIndex)(0).rotate(lq.inverse().asInstanceOf[QuaternionType])
    hands(handsPairIndex)(0).thumbVis.rotate(lqt.asInstanceOf[QuaternionType])
    hands(handsPairIndex)(0).indexVis.rotate(lqi.asInstanceOf[QuaternionType])
    hands(handsPairIndex)(0).middleVis.rotate(lqm.asInstanceOf[QuaternionType])
    hands(handsPairIndex)(0).ringVis.rotate(lqr.asInstanceOf[QuaternionType])
    hands(handsPairIndex)(0).littleVis.rotate(lql.asInstanceOf[QuaternionType])
  }

  val colors = List("green", "red", "blue", "orange", "purple", "teal")
  val colorMap = Map(colors.head -> 0xA1CF64, colors(1) -> 0xD95C5C, colors(2) -> 0x6ECFF5,
    colors(3) -> 0xF05940, colors(4) -> 0x564F8A, colors.tail -> 0x00B5AD)

  def randColorName: String = colors(Random.nextInt(colors.size))

  protected def nodeTagFromTitle(title: String, colorName: String) = textarea(title, `class` := s"ui large ${colorName} message").render

  var sprites = List.empty[HtmlSprite]

  override val controls: CameraControls = new VisualizationControls(camera, this.container, scene, width, height)

  val light = new DirectionalLight(0xffffff, 2)
  light.position.set(1, 1, 1).normalize()
  scene.add(light)

  // consider that 1. pair is the default one and second is referential
  val hands: Array[Array[HandVisualization[GeomType, QuaternionType]]] = Array(Array(
      new HandVisualization[GeomType, QuaternionType](Hand.LEFT, this),
      new HandVisualization[GeomType, QuaternionType](Hand.RIGHT, this)
    ), Array(
      new HandVisualization[GeomType, QuaternionType](Hand.LEFT, this),
      new HandVisualization[GeomType, QuaternionType](Hand.RIGHT, this)
    )
  )
  camera.setLens(3,1)

  for(handsPair <- hands) {
    handsPair(0).setLog(Log)
    handsPair(1).setLog(Log)
  }

  def drawBothHands(hands: Array[HandVisualization[GeomType, QuaternionType]], color: Color) = {
    val dot = new Object3D()
    scene.rotateY(Math.PI)
    dot.position.set( 170, -150, 0.0)
    scene.add(dot)

    hands(0).drawWholeHand(dot.asInstanceOf[GeomType])
    //hands(1).rotateY((Math.PI).toFloat)

    // left hand
    val dot2 = new Object3D()
    dot2.position.set( -170, -150, 0.0)
    scene.add(dot2)

    hands(1).drawWholeHand(dot2.asInstanceOf[GeomType])
  }

  def getHandsColor(i: Int): Color ={
    // TODO ...
    i match{
      case 0 => {
        return new Color(0,1,1)
      }
      case 1 => {
        return  new Color(1,0,1)
      }
    }
    new Color(1,0,0)
  }

  def drawAllHands(): Unit ={
    scene.rotateY(Math.PI)
    var i:Int = 0
    for(handsPair <- hands) {
      drawBothHands(handsPair, getHandsColor(i))
      i+=1
    }
  }

  /**
   * Clean all .. cause I am nasty and want to rape graphic resources but redrawing EVERYTHING
   * TODO change redrawing just changed values ... keep track of drawn objects (modularize parts)
   * this will require adding facade around VisualiyationContextT methods data and further changes of BaseVisualization
   **/
  @JSExport("cleanScene")
  def cleanScene(){
    while(scene.children.length > 0){
      scene.children(0)
      scene.remove(scene.children(0))
    }
  }

  @JSExport("rotatePart")
  def rotatePart(handsPair: Int, hi: Int, si: Int, x: Float, y: Float, z: Float){
    val h: Hand.Hand = if (hi == 0) Hand.RIGHT else Hand.LEFT
    val s: Sensor = Sensor.values(si)
    val hand = if (h == Hand.RIGHT) hands(handsPair)(1) else hands(handsPair)(0)
    //part.rotate(0,x,y,z)
    val part = hand.getBy(s)
    part.rotateX(x)
    part.rotateY(y)
    part.rotateZ(z)
  }
  drawAllHands()

  // Below are methods that implement with VisualizationContextT
  // docs https://threejs.org/docs/

  // Some notes for visualization:
  // ThreeJS does not implement native support for MatrixStack accessible via "scene"?
  // - alternative was described here: https://webglfundamentals.org/webgl/lessons/webgl-2d-matrix-stack.html
  // Well known true fact about matrixes: "Matrixes.. can't live with then. Can't simply describe world without them."
  // Translations are used for moving center point to other rotation for current matrix (m4s.stack.top)
  // RotationX over x does nothing for hand
  // RotationZ over z rotates to left/right
  // RotationY over y rotates around X axis (weird I know..)
  // Rotation should be used after translation (in other case it would rotate already translated position)

  // https://stackoverflow.com/questions/45189592/in-scala-js-how-to-best-create-an-object-conforming-to-a-trait-from-a-js-fa%C3%A7ade
  val dotMatParams = js.Dynamic.literal(
    color = 255.0
  ).asInstanceOf[MeshBasicMaterialParameters]

  val lineMatParams = js.Dynamic.literal(
    color = 255.0
  ).asInstanceOf[LineBasicMaterialParameters]

  override def _point(x: Float, y: Float, z: Float, geomHolder: Option[GeomType]): GeomType= {
    val geometry = new SphereGeometry( 5, 32, 32 )
    geometry.applyMatrix(new Matrix4().makeTranslation(x, y, z))
    val material = new MeshBasicMaterial(dotMatParams) //color = 0xffff00
    val sphere = new Mesh( geometry, material )
    val posObj = geomHolder.get
    if(posObj.isInstanceOf[Object3D]){
      val obj = posObj.asInstanceOf[Object3D]
      obj.add( sphere )
    }
    Log.dump(sphere, Log.Level.VIS_CONTEXT)
    sphere.asInstanceOf[GeomType]
  }

  override def _line(sx: Float, sy: Float, sz: Float, ex: Float, ey: Float, ez: Float
                     ,geomHolder: Option[GeomType]): GeomType = {
    val mat = new LineBasicMaterial(lineMatParams)
    val geo = new Geometry()
    geo.vertices.push(new Vector3(sx, sy, sz))
    geo.vertices.push(new Vector3(ex, ey, ez))
    val line = new Line(geo, mat)
    geomHolder.get.add(line)
    Log.dump(line, Log.Level.VIS_CONTEXT)
    line.asInstanceOf[GeomType]
  }

  override def _add(geom: GeomType, x:Float, y:Float, z:Float): Option[GeomType] = {
    val p = new Object3D()
    p.position.set(x,y,z)
    geom.asInstanceOf[Object3D].add( p )
    val opt = Option(p.asInstanceOf[GeomType])
    opt
  }

  // TODO use case class
  object AxisVector {
    type AxisVector = Vector3
     val X = new Vector3(1,0,0)
     val Y = new Vector3(0,1,0)
     val Z = new Vector3(0,0,1)
  }

  // https://discourse.threejs.org/t/how-do-you-rotate-a-group-of-objects-around-an-arbitrary-axis/3433/10
  // https://stackoverflow.com/questions/44287255/whats-the-right-way-to-rotate-an-object-around-a-point-in-three-js
  // TODO fix rotateOnAxis -> rotate as set not add
  override def _rotateGeoms(angle: Float, pivot:Option[GeomType], axis: Axis.Axis): Unit = {
    if(!pivot.isEmpty){
      val piv = pivot.get
      axis match {
        case Axis.X => {
          piv.rotateOnAxis(AxisVector.X, angle-piv.rotation.x)
        }
        case Axis.Y => {
          piv.rotateOnAxis(AxisVector.Y, angle-piv.rotation.y)
        }
        case Axis.Z => {
          piv.rotateOnAxis(AxisVector.Z, angle-piv.rotation.z)
        }
        case _ => {
          // Log.error("_rotateGeoms")
        }
      }
    }
  }

  override def _rotateGeoms(q: QuaternionType, pivot:Option[GeomType]): Unit = {
    if(!pivot.isEmpty){
      val piv = pivot.get
      piv.setRotationFromQuaternion(q)
    }
  }

}
// scalastyle: on

/**
 * Just shows that some effects are working
 * @param cam the camera control
 * @param el the html element
 * @param sc scene
 * @param center center of screen
 */

class VisualizationControls(cam: Camera, el: HTMLElement, sc: Scene, width: Double, height: Double,
                            center: Vector3 = new Vector3()) extends JumpCameraControls(cam, el, sc, width, height, center) {
  import org.querki.jquery._

  lazy val $el = $(el)

  override def onMouseMove(event: MouseEvent) = {
    val (offsetX, offsetY) = ($el.offset().left, $el.offset().top)
    this.onCursorMove(event.clientX - offsetX, event.clientY - offsetY, width, height)

    enter.keys.foreach {
      case m: Mesh =>
        m.material match {
          case mat: MeshLambertMaterial => mat.wireframe = true
          case _ => // do nothing
        }
      case _ => // do nothing
    }

    exit.keys.foreach {
      case m: Mesh =>
        m.material match {
          case mat: MeshLambertMaterial => mat.wireframe = false
          case _ => // do nothing
        }
      case _ => // do nothing
    }

    rotateOnMove(event)

  }

}

trait VisualizationData {

  lazy val myCode = "myCode"
  lazy val htmlCode = "htmlCode"

}