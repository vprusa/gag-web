package cz.muni.fi.gag.web.common

import cz.muni.fi.gag.web.common.shared.VisualizationContextT

object VisualizationContext extends VisualizationContextT {

  def _pushMatrix() = {}

  def _popMatrix()= {}

  def _point(x: Float, y: Float, z: Float)= {}

  def _rotate(angle: Float, rotationX: Float, rotationY: Float, rotationZ: Float)= {}

  def _stroke(v1: Float, v2: Float, v3: Float)= {}

  def _translate(x: Float, y: Float, z: Float)= {}

  def _line(sx: Float, sy: Float, sz: Float, ex: Float, ey: Float, ez: Float)= {}

  def _strokeWeight(w: Int)= {}

}
