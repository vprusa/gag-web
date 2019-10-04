package cz.muni.fi.gag.web.common.visualization

import cz.muni.fi.gag.web.common.Hand
import cz.muni.fi.gag.web.common.shared.VisualizationContext

class FingerVisualization(override val hi: Hand.type, override val app: VisualizationContext,
                          var sideOffset: Int, var length1: Int, var length2offset: Int, var length3offset: Int,
                          var length4offset: Int) extends HandVisualizationBase(hi, app) {
  var length2 = 0
  var length3 = 0
  var length4 = 0
  var offsetY = 0

  // for thumb
  def this(hi: Hand.type,app: VisualizationContext, sideOffset: Int, length1: Int, length2offset: Int, length3offset: Int) {
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

  def drawStaticPart() = app.lineWithDot(0, offsetY, 0, sideOffset, this.length1, 0)

  def drawParts() = { // drawStaticPart();
    // app.lineWithDot(fingerStartX, fingerStartY, 0, sideOffset, this.length1, 0);
    app.lineWithDot(sideOffset, this.length1, 0, sideOffset, this.length2, 0)
    if (this.length4 == -1 || this.length4offset == -1) {
      this.app.strokeWeight(1)
      this.app.line(sideOffset, this.length2, 0, sideOffset, this.length3, 0)
    }
    else {
      app.lineWithDot(sideOffset, this.length2, 0, sideOffset, this.length3, 0)
      this.app.strokeWeight(1)
      this.app.line(sideOffset, this.length3, 0, sideOffset, this.length4, 0)
    }
  }

  def draw() = {
    val length1bkp = this.length1
    val sideOffsetbkp = this.sideOffset
    drawStaticPart()
    // translate magic
    app.pushMatrix()
    app.translate(0, 0, 0)
    app.translate(sideOffset, this.length1, 0)
    this.length1 = 0
    recalcLenghts(this.length1, length2offset, length3offset, length4offset)
    /*
             * app.rotateX(rotationX * (hi.ordinal() == 0 ? 1f : -1f));
             * app.rotateY(rotationY * (hi.ordinal() == 0 ? 1f : -1f));
             * app.rotateZ(rotationZ);// * (hi.ordinal() == 0 ? 1f : -1f));
             */ val hiv = if (hi == Hand.LEFT) 1f
    else -(1f)
    app.rotate(angle, rotationX * hiv, rotationY * hiv, rotationZ * hiv)
    app.stroke(0, 255, 0)
    app.point(0, 0, 0)
    sideOffset = 0
    drawParts()
    app.translate(0, 0, 0)
    app.popMatrix()
    app.stroke(255, 0, 255) // purple
    this.length1 = length1bkp
    this.sideOffset = sideOffsetbkp
  }
}