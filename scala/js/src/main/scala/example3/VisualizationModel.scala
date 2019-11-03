package example3

import cz.muni.fi.gag.web.common.shared.VisualizationContextT
import cz.muni.fi.gag.web.common.visualization.HandVisualization
import cz.muni.fi.gag.web.common.{Hand, Log}
import org.denigma.threejs.{PerspectiveCamera, _}
import org.denigma.threejs.extensions.Container3D
import org.denigma.threejs.extensions.controls.{CameraControls, JumpCameraControls}
import org.denigma.threejs.extras.HtmlSprite
import org.scalajs
import org.scalajs.dom.MouseEvent
import org.scalajs.dom.raw.HTMLElement
import scalatags.JsDom.all._

import scala.collection.mutable
import scala.scalajs.js
import scala.scalajs.js.Dynamic
import scala.scalajs.js.annotation.JSExport
import scala.util.Random

// TODO rename to more self-explanatory name in given context
object VisualizationModel extends VisualizationData {
  def activate(): VisualizationScene = {
    val el: HTMLElement = scalajs.dom.document.getElementById("container").asInstanceOf[HTMLElement]
    val demo = new VisualizationScene(el, 300, 250)
    demo.render()
    demo
  }
}

class MatrixStack(val m4: Matrix4) {
  val original = m4.clone()
  val stack = new mutable.Stack[Matrix4]()
  //Predef.println("MatrixStack")
  //Predef.println(m4)
  pop()

  // Pops the top of the stack restoring the previously saved matrix
  def pop():Matrix4 = {
    Log.dump("pop", Log.Level.VIS_MATRIX_STACK)
    Log.dump(this.stack.length, Log.Level.VIS_MATRIX_STACK)
    // Never let the stack be totally empty
    if (this.stack.length < 1) {
      this.stack.push(m4.identity())
      Log.dump(this.stack.top, Log.Level.VIS_MATRIX_STACK)
      return this.stack.top
    }
    Log.dump(this.stack.top, Log.Level.VIS_MATRIX_STACK)
    this.stack.pop()
  }

  def push() = {
    Log.dump("push", Log.Level.VIS_MATRIX_STACK)
    //this.stack.push(getCurrentMatrix().clone())
    if(this.stack.length>0) {
      Log.dump(this.stack, Log.Level.VIS_MATRIX_STACK)
    }
    /*this.stack.push(new Matrix4().set(
      1,0,0,1,
      0 , 1,0,1,
      0, 0, 1, 1,
      0, 0, 0, 1))
    */
    //this.stack.push(original.identity().clone)
    this.stack.push(this.stack.top.clone())
  }

  def multiplyAll(matt: Matrix4): Matrix4 = {
    stack.foreach(matt multiply _)
    matt
  }

  def getCurrentMatrixAll(): Matrix4 = {
    multiplyAll(getCurrentMatrix())
  }
    // Gets current matrix (top of the stack)
  def getCurrentMatrix(): Matrix4 = {
    Log.dump("getCurrentMatrix", Log.Level.VIS_MATRIX_STACK)
    if(this.stack.length < 1) {
      this.pop()
    }
    Log.dump(this.stack.head, Log.Level.VIS_MATRIX_STACK)
    this.stack.top
  }

  // Lets us set the current matrix
  def setCurrentMatrix(m: Matrix4) {
    Log.dump("setCurrentMatrix", Log.Level.VIS_MATRIX_STACK)
    if(this.stack.length > 0){
      this.stack.pop()
    }
    this.stack.push(m)
    getCurrentMatrix()
  }

}

class VisualizationScene(val container: HTMLElement, val width: Double, val height: Double) extends Container3D with VisualizationContextT {

  /*
  * To avoid exception
  * THREE.WebGLRenderer.render: camera is not an instance of THREE.Camera
  * it is necessary to initCamera()
  */
  override def onEnterFrame(): Unit = {
    controls.update()
    if(!camera.isInstanceOf[PerspectiveCamera]){
      var cam = initCamera()
      renderer.render(scene, cam)
    } else {
      renderer.render(scene, camera)
    }
    cssRenderer.render(cssScene, camera)
  }

  override protected def initRenderer() = {
    var vr = super.initRenderer()
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
    drawBothHands()
  }

  @JSExport("updateAngles")
  def updateAngles(
         rx: Float = rightHandVis.rotationX,
         ry: Float = rightHandVis.rotationY,
         rz: Float = rightHandVis.rotationZ,

         rtx: Float = rightHandVis.thumpVis.rotationX,
         rty: Float = rightHandVis.thumpVis.rotationY,
         rtz: Float = rightHandVis.thumpVis.rotationZ,

         rix: Float = rightHandVis.indexVis.rotationX,
         riy: Float = rightHandVis.indexVis.rotationY,
         riz: Float = rightHandVis.indexVis.rotationZ,

         rmx: Float = rightHandVis.middleVis.rotationX,
         rmy: Float = rightHandVis.middleVis.rotationY,
         rmz: Float = rightHandVis.middleVis.rotationZ,

         rrx: Float = rightHandVis.ringVis.rotationX,
         rry: Float = rightHandVis.ringVis.rotationY,
         rrz: Float = rightHandVis.ringVis.rotationZ,

         rlx: Float = rightHandVis.littleVis.rotationX,
         rly: Float = rightHandVis.littleVis.rotationY,
         rlz: Float = rightHandVis.littleVis.rotationZ,

         lx: Float = leftHandVis.rotationX,
         ly: Float = leftHandVis.rotationY,
         lz: Float = leftHandVis.rotationZ,

         ltx: Float = leftHandVis.thumpVis.rotationX,
         lty: Float = leftHandVis.thumpVis.rotationY,
         ltz: Float = leftHandVis.thumpVis.rotationZ,

         lix: Float = leftHandVis.indexVis.rotationX,
         liy: Float = leftHandVis.indexVis.rotationY,
         liz: Float = leftHandVis.indexVis.rotationZ,

         lmx: Float = leftHandVis.middleVis.rotationX,
         lmy: Float = leftHandVis.middleVis.rotationY,
         lmz: Float = leftHandVis.middleVis.rotationZ,

         lrx: Float = leftHandVis.ringVis.rotationX,
         lry: Float = leftHandVis.ringVis.rotationY,
         lrz: Float = leftHandVis.ringVis.rotationZ,

         llx: Float = leftHandVis.littleVis.rotationX,
         lly: Float = leftHandVis.littleVis.rotationY,
         llz: Float = leftHandVis.littleVis.rotationZ
      ): Any = {
    rightHandVis.rotationX = rx
    rightHandVis.rotationY = ry
    rightHandVis.rotationZ = rz

    rightHandVis.thumpVis.rotationX = rtx
    rightHandVis.thumpVis.rotationY = rty
    rightHandVis.thumpVis.rotationZ = rtz

    rightHandVis.indexVis.rotationX = rix
    rightHandVis.indexVis.rotationY = riy
    rightHandVis.indexVis.rotationZ = riz

    rightHandVis.middleVis.rotationX = rmx
    rightHandVis.middleVis.rotationY = rmy
    rightHandVis.middleVis.rotationZ = rmz

    rightHandVis.ringVis.rotationX = rrx
    rightHandVis.ringVis.rotationY = rry
    rightHandVis.ringVis.rotationZ = rrz

    rightHandVis.littleVis.rotationX = rlx
    rightHandVis.littleVis.rotationY = rly
    rightHandVis.littleVis.rotationZ = rlz

    leftHandVis.rotationX = lx
    leftHandVis.rotationY = ly
    leftHandVis.rotationZ = lz

    leftHandVis.thumpVis.rotationX = ltx
    leftHandVis.thumpVis.rotationY = lty
    leftHandVis.thumpVis.rotationZ = ltz

    leftHandVis.indexVis.rotationX = lix
    leftHandVis.indexVis.rotationY = liy
    leftHandVis.indexVis.rotationZ = liz

    leftHandVis.middleVis.rotationX = lmx
    leftHandVis.middleVis.rotationY = lmy
    leftHandVis.middleVis.rotationZ = lmz

    leftHandVis.ringVis.rotationX = lrx
    leftHandVis.ringVis.rotationY = lry
    leftHandVis.ringVis.rotationZ = lrz

    leftHandVis.littleVis.rotationX = llx
    leftHandVis.littleVis.rotationY = lly
    leftHandVis.littleVis.rotationZ = llz
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

  var m4s = new MatrixStack(this.scene.matrix)

  var leftHandVis: HandVisualization = new HandVisualization(Hand.LEFT, this)
  var rightHandVis: HandVisualization = new HandVisualization(Hand.RIGHT, this)
  // TODO chekc if ok
  camera.setLens(3,1)

  def drawBothHands() = {
    Log.dump("rightHandVis", Log.Level.VIS_CONTEXT)
    // center point
    _pushMatrix()
    // shift down
    _translate(0,-150,0)
    // TODO +scale
    // center point
    _point(0,0,0)
    // right hand
    _pushMatrix()
    //_rotate(1,0,0,1)
    _translate(170,0,0)
    _rotateY((Math.PI).toFloat)
    rightHandVis.drawRotateByHand()
    _popMatrix()

    // left hand
    _pushMatrix()
    _translate(-170,0,0)
    //_rotateZ((Math.PI / 5.0f).toFloat)
    leftHandVis.drawRotateByHand()
    _popMatrix()
    _popMatrix()
  }

  /**
   * Clean all .. cause I am nasty and want to rape graphic resources but redrawing EVERYTHING
   * TODO change redrawing just changed values ... keep track of drawn objects (modularize parts)
   * this will require adding facade around VisualiyationContextT methods data and further changes of BaseVisualization
   **/
  @JSExport("cleanScene")
  def cleanScene(){
    while(scene.children.length > 0){
      scene.remove(scene.children(0))
    }
  }

  drawBothHands()

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

  override def _pushMatrix() = {
    // TODO this should get latest geometry?
    m4s.push()
  }

  override def _popMatrix()= {
    var popped = m4s.pop()
  }

  override def _point(x: Float, y: Float, z: Float)= {
    var geometry = new SphereGeometry( 5, 32, 32 );
    geometry.applyMatrix(new Matrix4().makeTranslation(x, y, z))
    var material = new MeshBasicMaterial(dotMatParams); //color = 0xffff00
    var sphere = new Mesh( geometry, material );
    sphere.applyMatrix(m4s.getCurrentMatrix())
    scene.add( sphere );
  }

  override def _rotate(angle: Float, rotationX: Float, rotationY: Float, rotationZ: Float)= {
    //m4s.setCurrentMatrix(m4s.getCurrentMatrix().makeRotationAxis(new Vector3(rotationX, rotationY, rotationZ), angle))
    var cur = m4s.getCurrentMatrix()
    cur = cur.makeRotationZ(rotationZ)
    m4s.setCurrentMatrix(cur)
  }

  override def _rotateX(rotationX: Float)= {
    m4s.setCurrentMatrix(m4s.getCurrentMatrix().multiply(m4s.original.identity().makeRotationX(rotationX)))
  }

  override def _rotateY(rotationY: Float)= {
    m4s.setCurrentMatrix(m4s.getCurrentMatrix().multiply(m4s.original.identity().makeRotationY(rotationY)))
  }

  override def _rotateZ(rotationZ: Float)= {
    m4s.setCurrentMatrix(m4s.getCurrentMatrix().multiply(m4s.original.identity().makeRotationZ(rotationZ)))
  }

  override def _stroke(v1: Float, v2: Float, v3: Float)= {
    // TODO this should get latest geometry?
  }

  /**
   * TODO
   * I admit that I do not know matrix operations that well so it is highly possible that there is a better way
   * (one-liner performance friendly and easy extensible)
   * */
  override def _translate(x: Float, y: Float, z: Float)= {
    // TODO
    Log.dump("translate", Log.Level.VIS_CONTEXT)
    var cur = m4s.getCurrentMatrix()
    var vec = new Vector3();
    vec.setFromMatrixPosition( cur );
    Log.dump("Old: x " + vec.x + " -> " + x + ", y " + vec.y + " -> " + y + ", z " +vec.z+" -> " + z, Log.Level.VIS_CONTEXT)
    m4s.setCurrentMatrix(m4s.getCurrentMatrix().multiply(m4s.original.identity().makeTranslation(x,y,z)))
    var newCur = m4s.getCurrentMatrix()
    vec = new Vector3();
    vec.setFromMatrixPosition( newCur );
    Log.dump("New: x " + vec.x + " -> " + x + ", y " + vec.y + " -> " + y + ", z " +vec.z+" -> " + z, Log.Level.VIS_CONTEXT)
  }

  override def _line(sx: Float, sy: Float, sz: Float, ex: Float, ey: Float, ez: Float)= {
    var mat = new LineBasicMaterial(lineMatParams)
    var geo = new Geometry()
    geo.vertices.push(new Vector3(sx, sy, sz))
    geo.vertices.push(new Vector3(ex, ey, ez))
    var line = new Line(geo, mat)
    line.applyMatrix(m4s.getCurrentMatrix())
    scene.add(line)
  }

  override def _strokeWeight(w: Int)= {
    // TODO this should get latest geometry?
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