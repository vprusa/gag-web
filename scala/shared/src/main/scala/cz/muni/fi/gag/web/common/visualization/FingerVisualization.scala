package cz.muni.fi.gag.web.common.visualization

import cz.muni.fi.gag.web.common.Hand
import cz.muni.fi.gag.web.common.shared.VisualizationContextT

class FingerVisualization(override val hi: Hand.Hand, override val app: VisualizationContextT,
                          var sideOffset: Int, var length1: Int, var length2offset: Int, var length3offset: Int,
                          var length4offset: Int) extends HandVisualizationBase(hi, app) {
  var length2 = 0
  var length3 = 0
  var length4 = 0
  var offsetY = 0

  // for thumb
  def this(hi: Hand.Hand, app: VisualizationContextT, sideOffset: Int, length1: Int, length2offset: Int, length3offset: Int) {
    this(hi,app, sideOffset, length1, length2offset, length3offset, -1)
    recalcLenghts(length1, length2offset, length3offset, length4offset)
  }

  private[visualization] def recalcLenghts(length1: Int, length2offset: Int, length3offset: Int, length4offset: Int) = {
    this.length1 = length1
    this.length2 = this.length1 + length2offset
    this.length3 = this.length2 + length3offset
    if (length4offset != -1) this.length4 = this.length3 + length4offset
    else this.length4 = -1
  }

  def drawStaticPart() = app._lineWithDot(0, offsetY, 0, sideOffset, this.length1, 0)

  def drawParts() = { // drawStaticPart();
    // app.lineWithDot(fingerStartX, fingerStartY, 0, sideOffset, this.length1, 0);
    app._lineWithDot(sideOffset, this.length1, 0, sideOffset, this.length2, 0)
    if (this.length4 == -1 || this.length4offset == -1) {
      this.app._strokeWeight(1)
      this.app._line(sideOffset, this.length2, 0, sideOffset, this.length3, 0)
    } else {
      app._lineWithDot(sideOffset, this.length2, 0, sideOffset, this.length3, 0)
      this.app._strokeWeight(1)
      this.app._line(sideOffset, this.length3, 0, sideOffset, this.length4, 0)
    }
  }

  def draw() = {
    val length1bkp = this.length1
    val sideOffsetbkp = this.sideOffset
    //app._translate(0, 0, 0)
    drawStaticPart()
    app._pushMatrix()
    //}
    //app._popMatrix()
    // translate magic
    //app._pushMatrix()
    //if(hi == Hand.LEFT) {
    //app._translate(0, 0, 0)
    app._translate(sideOffset, this.length1, 0)
    this.length1 = 0
    recalcLenghts(this.length1, length2offset, length3offset, length4offset)
    //val hiv = if (hi == Hand.LEFT) 1f else -(1f)
    val hiv = 1.0f
    //app._rotate(angle, rotationX * hiv, rotationY * hiv, rotationZ * hiv)
    //if(hi == Hand.RIGHT) {
      app._rotateZ(rotationZ)
      app._rotateY(rotationY)
      //app._rotateX(rotationX)
      //, 50, 50, 50, 50)
    //}
    //app._rotate(angle, rotationX * hiv, rotationY * hiv, rotationZ * hiv)
    app._stroke(0, 255, 0)
    app._point(0, 0, 0)
    sideOffset = 0
    drawParts()
    //app._translate(0, 0, 0)
    //if(hi == Hand.LEFT) {
    //Log.dump("IDK")
    //cz.muni.fi.gag.web.common.shared.Log.dump("idk")
    //}
    app._popMatrix()
    app._stroke(255, 0, 255) // purple
    this.length1 = length1bkp
    this.sideOffset = sideOffsetbkp
  }
}