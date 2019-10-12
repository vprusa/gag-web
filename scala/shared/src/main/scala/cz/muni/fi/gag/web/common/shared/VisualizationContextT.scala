package cz.muni.fi.gag.web.common.shared

trait VisualizationContextT {

  def _pushMatrix()

  def _popMatrix()

  def _point(x: Float, y: Float, z: Float)

  def _rotate(angle: Float, rotationX: Float, rotationY: Float, rotationZ: Float)

  def _stroke(v1: Float, v2: Float, v3: Float)

  def _translate(x: Float, y: Float, z: Float)

  //def line(sx: Int, sy: Int, sz: Int, ex: Int, ey: Int, ez: Int)

  def _line(sx: Float, sy: Float, sz: Float, ex: Float, ey: Float, ez: Float)

  def _strokeWeight(w: Int)

  def _lineWithDot(x1: Float, y1: Float, z1: Float, x2: Float, y2: Float, z2: Float): Unit = {
    _strokeWeight(1)
    _line(x1, y1, z1, x2, y2, z2)
    _strokeWeight(8)
    _point(x2, y2, z2)
  }

}
