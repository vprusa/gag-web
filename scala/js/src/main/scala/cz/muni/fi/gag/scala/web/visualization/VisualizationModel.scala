package cz.muni.fi.gag.scala.web.visualization

import cz.muni.fi.gag.scala.web.shared.Log
import cz.muni.fi.gag.web.scala.shared.Hand
import cz.muni.fi.gag.web.scala.shared.common.Sensor.Sensor
import cz.muni.fi.gag.web.scala.shared.common.{Axis, Sensor, VisualizationContextAbsImpl}
//import cz.muni.fi.gag.web.scala.shared.recognition.Sensor.Sensor
import cz.muni.fi.gag.web.scala.shared.visualization.HandVisualization
import org.denigma.threejs.extensions.Container3D
import org.denigma.threejs.extensions.controls.{CameraControls, JumpCameraControls}
import org.denigma.threejs.extras.HtmlSprite
import org.denigma.threejs.{Color, Object3D, PerspectiveCamera, _}
import org.scalajs.dom.MouseEvent
import org.scalajs.dom.raw.HTMLElement
import scalatags.JsDom.all._

import scala.collection.mutable.ArrayBuffer
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import scala.util.Random

class VisualizationScene[GeomType <: Object3DWithProps, QuaternionType <: Quaternion]
(val container: HTMLElement, val width: Double, val height: Double, var numberOfHandsPairs: Int = 1)
  extends Container3D {

  /*
  * To avoid exception
  * THREE.WebGLRenderer.render: camera is not an instance of THREE.Camera
  * it is necessary to initCamera()
  */
  override def onEnterFrame(): Unit = {
    controls.update()
    if (!camera.isInstanceOf[PerspectiveCamera]) {
      var cam = initCamera()
      renderer.render(scene, cam)
    } else {
      renderer.render(scene, camera)
    }
    cssRenderer.render(cssScene, camera)
  }

  // css
  override protected def initRenderer() = {
    val vr = super.initRenderer()
    vr.domElement.style.position = "relative"
    vr.domElement.style.display = "inline-block"
    vr.domElement.style.maxWidth = "100%"
    vr
  }

  @JSExport("updateMatrix")
  def updateMatrix(): Unit = {
    scene.updateMatrix()
  }

  @JSExport("renderAll")
  def renderAll(): Any = {
    cleanScene()
    drawAllHands()
    onEnterFrame()
    renderer.render(scene, camera)
  }

  var lq = new Quaternion(0.0f,0.0f,0.0f,0.0f)
  var lqt = new Quaternion(0.0f,0.0f,0.0f,0.0f)
  var lqi = new Quaternion(0.0f,0.0f,0.0f,0.0f)
  var lqm = new Quaternion(0.0f,0.0f,0.0f,0.0f)
  var lqr = new Quaternion(0.0f,0.0f,0.0f,0.0f)
  var lql = new Quaternion(0.0f,0.0f,0.0f,0.0f)

  // TODO TODO i always forget to write TODO here for quaternion rotation and not use '-rx'
  var rq = new Quaternion(0.0f,0.0f,0.0f,0.0f)
  var rqt = new Quaternion(0.0f,0.0f,0.0f,0.0f)
  var rqi = new Quaternion(0.0f,0.0f,0.0f,0.0f)
  var rqm = new Quaternion(0.0f,0.0f,0.0f,0.0f)
  var rqr = new Quaternion(0.0f,0.0f,0.0f,0.0f)
  var rql = new Quaternion(0.0f,0.0f,0.0f,0.0f)
  var rqC = new Quaternion(0.0f,0.0f,0.0f,0.0f)
  //  var rqC = new Quaternion(-rq.x, rq.y, -rq.z, rq.w).normalize() //rq.normalize() //.conjugate() //.inverse()

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
                   lix: Float, liy: Float, liz: Float, liw: Float,
                   lmx: Float, lmy: Float, lmz: Float, lmw: Float,
                   lrx: Float, lry: Float, lrz: Float, lrw: Float,
                   llx: Float, lly: Float, llz: Float, llw: Float
                  ): Any = {
    Log.dump("updateAngles", Log.Level.VIS_MODEL)
    /*
    Log.dump("updateAngles", Log.Level.VIS_MODEL)
    var lq = new Quaternion(lx, ly, lz, lw)
    var lqt = new Quaternion(ltx, lty, ltz, ltw)
    var lqi = new Quaternion(lix, liy, liz, liw)
    var lqm = new Quaternion(lmx, lmy, lmz, lmw)
    var lqr = new Quaternion(lrx, lry, lrz, lrw)
    var lql = new Quaternion(llx, lly, llz, llw)

    // TODO TODO i always forget to write TODO here for quaternion rotation and not use '-rx'
    var rq = new Quaternion(ry, -rx, rz, rw)
    var rqt = new Quaternion(rtx, rty, rtz, rtw)
    var rqi = new Quaternion(rix, riy, riz, riw)
    var rqm = new Quaternion(rmx, rmy, rmz, rmw)
    var rqr = new Quaternion(rrx, rry, rrz, rrw)
    var rql = new Quaternion(rlx, rly, rlz, rlw)
     */
    lq.set(lx, ly, lz, lw)
    lqt.set(ltx, lty, ltz, ltw)
    lqi.set(lix, liy, liz, liw)
    lqm.set(lmx, lmy, lmz, lmw)
    lqr.set(lrx, lry, lrz, lrw)
    lql.set(llx, lly, llz, llw)

    // TODO TODO i always forget to write TODO here for quaternion rotation and not use '-rx'
    rq.set(ry, -rx, rz, rw)
    rqt.set(rtx, rty, rtz, rtw)
    rqi.set(rix, riy, riz, riw)
    rqm.set(rmx, rmy, rmz, rmw)
    rqr.set(rrx, rry, rrz, rrw)
    rql.set(rlx, rly, rlz, rlw)
    rqt = rqt.normalize()
    rqi = rqi.normalize()
    rqm = rqm.normalize()
    rqr = rqr.normalize()
    rql = rql.normalize()

    //val rqC = rq.conjugate().inverse()
    //val rqC = rq.clone()
    rq = rq.normalize()
    //    var rqC = new Quaternion(-rq.x,-rq.y,-rq.z,rq.w).normalize() //rq.normalize() //.conjugate() //.inverse()
    rqC = rqC.set(-rq.x,-rq.y,-rq.z,rq.w).normalize()
    rql.set(rlx, rly, rlz, rlw)

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


  @JSExport("getNumberOfHandsPairs")
  def getNumberOfHandsPairs(): Int = {
    numberOfHandsPairs
  }

  @JSExport("setNumberOfHandsPairs")
  def setNumberOfHandsPairs(i: Int): Unit = {
    numberOfHandsPairs = i
    // TODO redraw hands (only new?) , if -lt prev then remove from the newest/lowest
  }

  // TODO move this outside of this class?
  val vcai = new VisualizationContextAbsImpl[GeomType, QuaternionType]() {
//    override def _rotateGeoms(angle: Float, pivot: Option[GeomType], axis: Axis.Axis): Unit = ???
//    override def _add(geom: GeomType, x: Float, y: Float, z: Float): Option[GeomType] = ???
//    override def _point(fl: Float, fl1: Float, fl2: Float, geomHolder: Option[GeomType]): GeomType = ???
//    override def _line(sx: Float, sy: Float, sz: Float, ex: Float, ey: Float, ez: Float, geomHolder: Option[GeomType]): GeomType = ???
//    override def _rotateGeoms(q: QuaternionType, pivot: Option[GeomType]): Unit = ???

    // TODO refactor
    override def _point(x: Float, y: Float, z: Float, geomHolderOpt: Option[GeomType]): GeomType = {
      def calcRadius(_geomHolderOpt: Option[GeomType]): Double = {
        val radius = 5
        if (_geomHolderOpt.nonEmpty) {
          val _geomHolder = _geomHolderOpt.get
          if (_geomHolder.isInstanceOf[MeshWithProps]) {
            val visSize = _geomHolder.asInstanceOf[MeshWithProps].props.visualizationSize
            return visSize.asInstanceOf[Double] * radius / 2
          } else if (_geomHolder.isInstanceOf[Object3DWithProps]) {
            val visSize = _geomHolder.asInstanceOf[Object3DWithProps].props.visualizationSize
            return visSize.asInstanceOf[Double] * radius / 2
            //        } else if (_geomHolder.isInstanceOf[Object3D]) {
            //          posObj.asInstanceOf[Object3D]
          }
        }
        radius
      }

      val radius = calcRadius(geomHolderOpt)
      val geometry = new SphereGeometry(radius, 32, 32)
      Log.dump("geometry", Log.Level.VIS_CONTEXT)
      Log.dump(geometry, Log.Level.VIS_CONTEXT)
      Log.dump("radius: " + radius, Log.Level.VIS_CONTEXT)

      geometry.applyMatrix(new Matrix4().makeTranslation(x, y, z))
      //    val material = new MeshBasicMaterial(dotMatParams) //color = 0xffff00
      val mat = new MeshBasicMaterial()
      if (geomHolderOpt.nonEmpty) {
        val geomHandler = geomHolderOpt.get
        mat.color = geomHandler.color
        mat.opacity = geomHandler.opacity.asInstanceOf[Double]
        mat.transparent = true
      }

      val sphere = new MeshWithProps(geometry, mat, (if (geomHolderOpt.isEmpty) handsPropsDefault else geomHolderOpt.get.props))

      if (geomHolderOpt.nonEmpty) {
        val posObj = geomHolderOpt.get
        Log.dump("posObj", Log.Level.VIS_CONTEXT)
        Log.dump(posObj, Log.Level.VIS_CONTEXT)

        val geomHolder = geomHolderOpt.get
        sphere.setProps(geomHolder.props)

        Log.dump("beforeAdd", Log.Level.VIS_CONTEXT)
        if (posObj.isInstanceOf[MeshWithProps]) {
          posObj.asInstanceOf[MeshWithProps].add(sphere)
        } else if (posObj.isInstanceOf[LineWithProps]) {
          posObj.asInstanceOf[LineWithProps].add(sphere)
        } else if (posObj.isInstanceOf[Object3DWithProps]) {
          posObj.asInstanceOf[Object3DWithProps].add(sphere)
        } else if (posObj.isInstanceOf[Object3D]) {
          posObj.asInstanceOf[Object3D].add(sphere)
        }
        Log.dump("afterAdd", Log.Level.VIS_CONTEXT)
      }

      Log.dump("sphere", Log.Level.VIS_CONTEXT)
      Log.dump(sphere, Log.Level.VIS_CONTEXT)

      sphere.asInstanceOf[GeomType]
    }

    // TODO refactor
    override def _line(sx: Float, sy: Float, sz: Float, ex: Float, ey: Float, ez: Float,
                       geomHolderOpt: Option[GeomType]): GeomType = {
      val mat = new LineBasicMaterial()
      if (geomHolderOpt.nonEmpty) {
        val geomHolder = geomHolderOpt.get
        mat.color = geomHolder.color
        mat.opacity = geomHolder.opacity.asInstanceOf[Double]
        mat.transparent = true
        mat.linewidth = geomHolder.visualizationSize.asInstanceOf[Double]
      }

      val geo = new Geometry()

      geo.vertices.push(new Vector3(sx, sy, sz))
      geo.vertices.push(new Vector3(ex, ey, ez))
      val line = new LineWithProps(geo, mat, (if (geomHolderOpt.isEmpty) handsPropsDefault else geomHolderOpt.get.props))
      //    geomHolderOpt.get.add(line.asInstanceOf[Line])
      if (geomHolderOpt.nonEmpty) {
        val geomHolder = geomHolderOpt.get
        if (geomHolder.isInstanceOf[MeshWithProps]) {
          geomHolder.asInstanceOf[MeshWithProps].add(line)
        } else if (geomHolder.isInstanceOf[LineWithProps]) {
          geomHolder.asInstanceOf[LineWithProps].add(line)
        } else if (geomHolder.isInstanceOf[Object3DWithProps]) {
          geomHolder.asInstanceOf[Object3DWithProps].add(line)
        } else if (geomHolder.isInstanceOf[Object3D]) {
          geomHolder.asInstanceOf[Object3D].add(line)
        }
      }
      Log.dump("line", Log.Level.VIS_CONTEXT)
      Log.dump(line, Log.Level.VIS_CONTEXT)

      line.asInstanceOf[GeomType]
    }

    // TODO refactor
    override def _add(geom: GeomType, x: Float, y: Float, z: Float): Option[GeomType] = {
      //    val p = new Object3DWithProps(geom)
      val p = new Object3DWithProps(geom)
      Log.dump("_add", Log.Level.VIS_CONTEXT)
      Log.dump(p, Log.Level.VIS_CONTEXT)
      Log.dump("_add-geom", Log.Level.VIS_CONTEXT)
      Log.dump(geom, Log.Level.VIS_CONTEXT)

      p.asInstanceOf[Object3DWithProps].position.set(x, y, z)

      p.setProps(geom.props)

      if (geom.isInstanceOf[MeshWithProps]) {
        geom.asInstanceOf[MeshWithProps].add(p)
      } else if (geom.isInstanceOf[LineWithProps]) {
        geom.asInstanceOf[LineWithProps].add(p)
      } else if (geom.isInstanceOf[Object3DWithProps]) {
        geom.asInstanceOf[Object3DWithProps].add(p)
      } else if (geom.isInstanceOf[Object3D]) {
        geom.asInstanceOf[Object3D].add(p)
      }

      val opt = Option(p.asInstanceOf[GeomType])
      opt
    }

    // TODO use case class
    object AxisVector {
      type AxisVector = Vector3
      val X = new Vector3(1, 0, 0)
      val Y = new Vector3(0, 1, 0)
      val Z = new Vector3(0, 0, 1)
    }

    // https://discourse.threejs.org/t/how-do-you-rotate-a-group-of-objects-around-an-arbitrary-axis/3433/10
    // https://stackoverflow.com/questions/44287255/whats-the-right-way-to-rotate-an-object-around-a-point-in-three-js
    // TODO fix rotateOnAxis -> rotate as set not add
    // TODO refactor these terrible matches with values.. the actual problem is [GeomType <: Object3DWithProps]
    // with too many second-hand extensions such as MeshWithProps
    // the same goes for _add, _line and everywhere where there is code containing
    //  "geom.isInstanceOf\[[MeshWithProps|LineWithProps|Object3DWithProps|Objecdt3D]\]"
    // as far as i know there is a limit to what can scala(js) do behind this (smth to do with being unable to extend
    // from one of the parent classes of js.Object)
    // solution to that might be a wrapper class but idk if that would not break on getting variables from its instances
    // when using threejs library
//    def _rotateGeoms(angle: Float, pivot: Option[GeomType], axis: Axis.AxisableVal): Unit
    override def _rotateGeoms(angle: Float, pivotOpt: Option[GeomType], axis: Axis.AxisableVal): Unit = {
    //    override def _rotateGeoms(angle: Float, pivotOpt: Option[GeomType], axis: Axisable): Unit = {
      if (!pivotOpt.isEmpty) {
        val pivotP = pivotOpt.get

        def getPivotVal(_pivot: GeomType, _axis: Axis.AxisableVal): Double = {
          if (_pivot.isInstanceOf[MeshWithProps]) {
            val p = _pivot.asInstanceOf[MeshWithProps]
            _axis match {
              case Axis.X => {
                return p.rotation.x
              }
              case Axis.Y => {
                return p.rotation.y
              }
              case Axis.Z => {
                return p.rotation.z
              }
              case _ => {
                return 0
              }
            }
          } else if (_pivot.isInstanceOf[Object3DWithProps]) {
            val p = _pivot.asInstanceOf[Object3DWithProps]
            _axis match {
              case Axis.X => {
                return p.rotation.x
              }
              case Axis.Y => {
                return p.rotation.y
              }
              case Axis.Z => {
                return p.rotation.z
              }
              case _ => {
                return 0
              }
            }
//          } else if (_pivot.isInstanceOf[Object3D]) {
//              pivot = pivot.asInstanceOf[Object3D]
          }
          return 0
        }

        axis match {
          case Axis.X => {
            pivotP.rotateOnAxis(AxisVector.X, angle - getPivotVal(pivotP, Axis.X))
          }
          case Axis.Y => {
            pivotP.rotateOnAxis(AxisVector.Y, angle - getPivotVal(pivotP, Axis.Y))
          }
          case Axis.Z => {
            pivotP.rotateOnAxis(AxisVector.Z, angle - getPivotVal(pivotP, Axis.Z))
          }
          case _ => {
            // Log.error("_rotateGeoms")
          }
        }
      }
    }

    override def _rotateGeoms(q: QuaternionType, pivot: Option[GeomType]): Unit = {
      if (!pivot.isEmpty) {
        val piv = pivot.get
        piv.setRotationFromQuaternion(q)
      }
    }
  }


  // TODO refactor not to use tight-coupling
  // consider that 1. pair is the default one and second is referential
  var hands: ArrayBuffer[Array[HandVisualization[GeomType, QuaternionType]]] = ArrayBuffer[Array[HandVisualization[GeomType, QuaternionType]]]()

  for (i <- 1 to numberOfHandsPairs) {
    hands += Array[HandVisualization[GeomType, QuaternionType]](
      new HandVisualization[GeomType, QuaternionType](Hand.LEFT, vcai),
      new HandVisualization[GeomType, QuaternionType](Hand.RIGHT, vcai)
    )
  }

  for (handsPair <- hands) {
    handsPair(Hand.LEFT.id).setLog(Log)
    handsPair(Hand.RIGHT.id).setLog(Log)
  }

  camera.setLens(3, 1)


  val handsColorDefault = new Color(0xFFFFFF)
  val handsColorRef = new Color(0x00FFFF)

  var handsOpacityDefault: scala.Double = (1.0: scala.Double)
  var handsOpacityRef: scala.Double = (0.5: scala.Double)

  var visualizationSizeDefault: Int = 1
  var visualizationSizeRef: Int = 5


  val handsPropsDefault = new Object3DWithProps(PropsJsTrait.newProps(handsColorDefault, visualizationSizeDefault, visualizationSizeDefault))
  val handsPropsRef = new Object3DWithProps(PropsJsTrait.newProps(handsColorRef, handsOpacityRef, visualizationSizeRef))

  def drawBothHands(hands: Array[HandVisualization[GeomType, QuaternionType]], props: Object3DWithProps) = {
    Log.dump("drawBothHands - props: ", Log.Level.VIS_MODEL)
    Log.dump(props, Log.Level.VIS_MODEL)
    val dot = new Object3DWithProps(props)

    dot.position.set(170, -150, 0.0)
    Log.dump("add dot", Log.Level.VIS_MODEL)
    Log.dump(dot, Log.Level.VIS_MODEL)
    scene.add(dot)

    hands(Hand.LEFT.id).drawWholeHand(dot.asInstanceOf[GeomType])
    //hands(1).rotateY((Math.PI).toFloat)

    // left hand
    val dot2 = new Object3DWithProps(props)
    //    val dot2 = props
    dot2.position.set(-170, -150, 0.0)
    Log.dump("add dot2", Log.Level.VIS_MODEL)
    Log.dump(dot2, Log.Level.VIS_MODEL)
    scene.add(dot2)

    hands(Hand.RIGHT.id).drawWholeHand(dot2.asInstanceOf[GeomType])
  }

  def getHandsProps(i: Int): Object3DWithProps = {
    // TODO ...
    i match {
      case 0 => {
        return handsPropsDefault
      }
      case 1 => {
        return handsPropsRef
      }
    }
    handsPropsDefault
  }

  def drawAllHands(): Unit = {
    scene.rotateY(Math.PI)
    //    scene.rotateY(Math.PI)

    var i: Int = 0
    for (handsPair <- hands) {
      drawBothHands(handsPair, getHandsProps(i))
      i += 1
    }
  }

  /**
   * Clean all .. cause I am nasty and want to rape graphic resources but redrawing EVERYTHING
   * TODO change redrawing just changed values ... keep track of drawn objects (modularize parts)
   * this will require adding facade around VisualiyationContextT methods data and further changes of BaseVisualization
   **/
  @JSExport("cleanScene")
  def cleanScene() {
    while (scene.children.length > 0) {
      scene.children(0)
      scene.remove(scene.children(0))
    }
  }

  @JSExport("rotatePart")
  def rotatePart(handsPair: Int, hi: Int, si: Int, x: Float, y: Float, z: Float) {
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
    color = 0xFFFF00
  ).asInstanceOf[MeshBasicMaterialParameters]

  val lineMatParams = js.Dynamic.literal(
    color = 0xFF33FF
  ).asInstanceOf[LineBasicMaterialParameters]

  val lineMatParamColor = new Color(0xFFFF00)

}

// scalastyle: on

/**
 * Just shows that some effects are working
 *
 * @param cam    the camera control
 * @param el     the html element
 * @param sc     scene
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