package cz.muni.fi.gag.web.common.shared

trait VisualizationContextT {
  def pushMatrix()

  def popMatrix()

  def point(x: Float, y: Float, z: Float)

  def rotate(angle: Float, rotationX: Float, rotationY: Float, rotationZ: Float)

  def stroke(v1: Float, v2: Float, v3: Float)

  def translate(x: Float, y: Float, z: Float)

  //def line(sx: Int, sy: Int, sz: Int, ex: Int, ey: Int, ez: Int)

  def line(sx: Float, sy: Float, sz: Float, ex: Float, ey: Float, ez: Float)

  def strokeWeight(w: Int)

  def lineWithDot(x1: Float, y1: Float, z1: Float, x2: Float, y2: Float, z2: Float): Unit = {
    strokeWeight(1)
    line(x1, y1, z1, x2, y2, z2)
    strokeWeight(8)
    point(x2, y2, z2)
  }

}
