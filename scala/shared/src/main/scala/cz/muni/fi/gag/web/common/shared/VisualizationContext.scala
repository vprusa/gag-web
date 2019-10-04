package cz.muni.fi.gag.web.common.shared

trait VisualizationContext {
  def pushMatrix()

  def popMatrix()

  def point(i: Int, i1: Int, i2: Int)

  def rotate(angle: Float, fl: Float, fl1: Float, fl2: Float)

  def stroke(i: Int, i1: Int, i2: Int)

  def translate(i: Int, i1: Int, i2: Int)

  def line(sx: Int, sy: Int, sz: Int, ex: Int, ey: Int, ez: Int)

  def line(sx: Float, sy: Float, sz: Float, ex: Float, ey: Float, ez: Float)

  def strokeWeight(i: Int)

  def lineWithDot(i: Int, offsetY: Int, i1: Int, sideOffset: Int, length1: Any, i2: Int)

}
