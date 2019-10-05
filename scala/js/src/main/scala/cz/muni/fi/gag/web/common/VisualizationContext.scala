package cz.muni.fi.gag.web.common

import cz.muni.fi.gag.web.common.shared.VisualizationContextT

object VisualizationContext extends VisualizationContextT {

  def pushMatrix() = {}

  def popMatrix()= {}

  def point(x: Float, y: Float, z: Float)= {}

  def rotate(angle: Float, rotationX: Float, rotationY: Float, rotationZ: Float)= {}

  def stroke(v1: Float, v2: Float, v3: Float)= {}

  def translate(x: Float, y: Float, z: Float)= {}

  def line(sx: Float, sy: Float, sz: Float, ex: Float, ey: Float, ez: Float)= {}

  def strokeWeight(w: Int)= {}

}
