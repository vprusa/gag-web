package example3

import cz.muni.fi.gag.web.common.Hand
import cz.muni.fi.gag.web.common.shared.VisualizationContextT
import cz.muni.fi.gag.web.common.visualization.HandVisualization
import org.denigma.threejs._
import org.denigma.threejs.extensions.Container3D
import org.denigma.threejs.extensions.controls.{CameraControls, JumpCameraControls}
import org.denigma.threejs.extras.HtmlSprite
import org.scalajs.dom
import org.scalajs.dom.MouseEvent
import org.scalajs.dom.raw.HTMLElement
import scalatags.JsDom.all._

import scala.collection.mutable
import scala.scalajs.js
import scala.util.Random

// TODO rename to more self-explanatory name in given context
object VisualizationModel extends VisualizationData {
  def activate(): Unit = {
    val el: HTMLElement = dom.document.getElementById("container").asInstanceOf[HTMLElement]
    val demo = new VisualizationScene(el, 1280, 500) // scalastyle:ignore
    demo.render()
  }
}

class MatrixStack(var m4: Matrix4) {
  val stack = new mutable.Stack[Matrix4]()
  restore()

  // Pops the top of the stack restoring the previously saved matrix
  def restore():Matrix4 = {
    // Never let the stack be totally empty
    if (this.stack.length < 1) {
      //this.stack[0] = m4.identity();
      this.stack.push(m4.identity())
      m4.identity()
    }
    this.stack.top
  }

  // Pushes a copy of the current matrix on the stack
  def save() = {
    this.stack.push(getCurrentMatrix())
  }

  // Gets a copy of the current matrix (top of the stack)
  def getCurrentMatrix(): Matrix4 = {
    this.stack.top.clone()
  }

  // Lets us set the current matrix
  def setCurrentMatrix(m: Matrix4) {
    this.stack.pop()
    this.stack.push(m)
    getCurrentMatrix()
  }

/*
  // Translates the current matrix
  def translate(x: Double, y:Double, z :Double ) {
    var m = this.getCurrentMatrix();
    //this.setCurrentMatrix(m4.translate(m, x, y, z));
    this.setCurrentMatrix(m.makeTranslation(x, y, z));
  };

  // Rotates the current matrix around Z
  MatrixStack.prototype.rotateZ = function(angleInRadians) {
    var m = this.getCurrentMatrix();
    this.setCurrentMatrix(m4.zRotate(m, angleInRadians));
  };

  // Scales the current matrix
  MatrixStack.prototype.scale = function(x, y, z) {
    var m = this.getCurrentMatrix();
    this.setCurrentMatrix(m4.scale(m, x, y, z));
  };
*/
}

// scalastyle:off
class VisualizationScene(val container: HTMLElement, val width: Double, val height: Double) extends Container3D with VisualizationContextT {

  val geometry = new BoxGeometry(350, 300, 250)

  val colors = List("green", "red", "blue", "orange", "purple", "teal")
  val colorMap = Map(colors.head -> 0xA1CF64, colors(1) -> 0xD95C5C, colors(2) -> 0x6ECFF5,
    colors(3) -> 0xF05940, colors(4) -> 0x564F8A, colors.tail -> 0x00B5AD)

  def materialParams(name: String): MeshLambertMaterialParameters = js.Dynamic.literal(
    color = new Color(colorMap(name)) // wireframe = true
    ).asInstanceOf[MeshLambertMaterialParameters]

  def randColorName: String = colors(Random.nextInt(colors.size))

  //protected def nodeTagFromTitle(title: String, colorName: String) = textarea(title, `class` := s"ui large ${colorName} message").render
  //protected def nodeTagFromTitle(title: String, colorName: String) = textarea(title, `class` := s"ui large ${colorName} message").render
  protected def nodeTagFromTitle(title: String, colorName: String) = textarea(title, `class` := s"ui large ${colorName} message").render

  // var meshes = addMesh(new Vector3(0, 0, 0)) :: addMesh(new Vector3(400, 0, 200)) :: addMesh(new Vector3(-400, 0, 200)) :: Nil
  var meshes = addMesh(new Vector3(0, 0, 0)) :: Nil

  var sprites = List.empty[HtmlSprite]

  override val controls: CameraControls = new VisualizationControls(camera, this.container, scene, width, height, this.meshes.head.position.clone())

  val light = new DirectionalLight(0xffffff, 2)
  light.position.set(1, 1, 1).normalize()
  scene.add(light)

  def addMesh(pos: Vector3 = new Vector3()): Mesh = {
    val material = new MeshLambertMaterial(this.materialParams(randColorName))
    val mesh: Mesh = new Mesh(geometry, material)
    mesh.name = pos.toString
    mesh.position.set(pos.x, pos.y, pos.z)
    mesh
  }

  def addLabel(pos: Vector3, title: String = "hello three.js and ScalaJS!"): HtmlSprite = {
    val helloHtml = nodeTagFromTitle(title, randColorName)
    val html = new HtmlSprite(helloHtml)
    html.position.set(pos.x, pos.y, pos.z)
    html
  }

  //var m4s = new MatrixStack(scene.matrix)
  var m4s = new MatrixStack(new Matrix4())

  //var leftHandVis: HandVisualization = new HandVisualization(Hand.LEFT, this)
  var rightHandVis: HandVisualization = new HandVisualization(Hand.RIGHT, this)

  var rightHandGeom = new BoxGeometry(350, 300, 250)

  //leftHandVis.draw()
  rightHandVis.draw()
  /*
  meshes.foreach(scene.add)
  meshes.zipWithIndex.foreach {
    case (m, i) =>
      this.sprites = addLabel(m.position.clone().setY(m.position.y + 200), "Text #" + i) :: this.sprites
  }
  sprites.foreach(cssScene.add)
  */

  // Below are methods that implement with VisualizationContextT
  // docs https://threejs.org/docs/

  // https://stackoverflow.com/questions/45189592/in-scala-js-how-to-best-create-an-object-conforming-to-a-trait-from-a-js-fa%C3%A7ade
  val dotMatParams = js.Dynamic.literal(
    color = 255.0
  ).asInstanceOf[MeshBasicMaterialParameters]

  val lineMatParams = js.Dynamic.literal(
    color = 255.0
  ).asInstanceOf[LineBasicMaterialParameters]

  override def _pushMatrix() = {
    // TODO this should get latest geometry?
    m4s.save()
  }

  override def _popMatrix()= {
    // TODO this should get latest geometry?
    scene.applyMatrix(m4s.restore())
  }

  override def _point(x: Float, y: Float, z: Float)= {
    var geometry = new SphereGeometry( 5, 32, 32 );
    geometry.applyMatrix(new Matrix4().makeTranslation(x, y, z))
    var material = new MeshBasicMaterial(dotMatParams); //color = 0xffff00
    var sphere = new Mesh( geometry, material );
    scene.add( sphere );
  }

  override def _rotate(angle: Float, rotationX: Float, rotationY: Float, rotationZ: Float)= {
    scene.matrix = m4s.getCurrentMatrix()
    scene.rotateOnAxis(new Vector3(rotationX, rotationY, rotationZ), angle);
  }

  override def _stroke(v1: Float, v2: Float, v3: Float)= {
    // TODO this should get latest geometry?
  }

  override def _translate(x: Float, y: Float, z: Float)= {
    // TODO one-liner
    //scene.matrix = m4s.getCurrentMatrix()
    scene.translateX(x);
    scene.translateY(y);
    scene.translateZ(z);
  }

  override def _line(sx: Float, sy: Float, sz: Float, ex: Float, ey: Float, ez: Float)= {
    var mat = new LineBasicMaterial(lineMatParams)
    var geo = new Geometry()
    geo.vertices.push(new Vector3(sx, sy, sz))
    geo.vertices.push(new Vector3(ex, ey, ez))
    var line = new Line(geo, mat)
    scene.matrix = m4s.getCurrentMatrix()
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