package cz.muni.fi.gag.web.common.visualization

import cz.muni.fi.gag.web.common.Hand
import cz.muni.fi.gag.web.common.shared.VisualizationContextT

class HandVisualizationBase(val hi: Hand.Hand, val app: VisualizationContextT) //this.app = Configuration.app();
{
  var rotationX = 0.0f
  var rotationY = 0.0f
  var rotationZ = 0.0f
  var angle = 0.0f
  //protected var app = null

  def rotate(angle: Float, rotationX: Float, rotationY: Float, rotationZ: Float) = {
    this.angle = angle
    this.rotationX = rotationX
    this.rotationY = rotationY
    this.rotationZ = rotationZ
  }

  def rotateX(rotationX: Float) = this.rotationX = rotationX

  def rotateY(rotationY: Float) = this.rotationY = rotationY

  def rotateZ(rotationZ: Float) = this.rotationZ = rotationZ

  def rotateAngle(angle: Float) = this.angle = angle
}