package cz.muni.fi.gag.web.common.visualization

import cz.muni.fi.gag.web.common.Hand
import cz.muni.fi.gag.web.common.shared.VisualizationContextT

class HandVisualization(override val hi: Hand.type, override val app: VisualizationContextT) extends HandVisualizationBase(hi, app) {
  var thumpVis = new FingerVisualization(hi, app, 50, 50, 50, 50)
  var indexVis = new FingerVisualization(hi, app, 25, 112, 60, 45, 30)
  var middleVis = new FingerVisualization(hi, app, 0, 120, 70, 50, 35)
  var ringVis = new FingerVisualization(hi, app, -25, 112, 65, 48, 30)
  var littleVis = new FingerVisualization(hi, app, -50, 102, 45, 35, 25)

  def draw() = {
    // thumpVis.rotateX(0.3f);
    thumpVis.draw()
    // indexVis.rotateZ(-0.5f);
    indexVis.draw()
    // no rotation in Y ;)
    // middleVis.rotateY(-0.5f);
    middleVis.draw()
    // ringVis.drawStaticPart();
    // ringVis.drawParts();
    ringVis.draw()
    // littleVis.drawStaticPart();
    // littleVis.drawParts();
    littleVis.draw()
  }

  override def rotate(angle: Float, rotationX: Float, rotationY: Float, rotationZ: Float) = {
    super.rotate(angle, rotationX, rotationY, rotationZ)
    // translate magic
    app._pushMatrix
    app._translate(0, 0, 0)
    /*
     * app.rotateX(rotationX * (hi.ordinal() == 0 ? 1f : -1f));
     * app.rotateY(rotationY * (hi.ordinal() == 0 ? 1f : -1f));
     * app.rotateZ(rotationZ * (hi.ordinal() == 0 ? 1f : -1f));
    */
    val hiv = if ((hi eq Hand.LEFT)) 1f else -(1f)
    app._rotate(angle, rotationX * hiv, rotationY * hiv, rotationZ * hiv)
    draw()
    app._translate(0, 0, 0)
    app._popMatrix
  }
}