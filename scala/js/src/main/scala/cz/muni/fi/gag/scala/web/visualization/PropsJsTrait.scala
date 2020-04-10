package cz.muni.fi.gag.scala.web.visualization

import org.denigma.threejs.{Color}


object PropsJsTrait {
  def newProps(_color: Color, _opacity: AnyVal, _visualizationSize: AnyVal): PropsJsTrait = new PropsJsTrait() {
    override var color: Color = _color
    override var opacity: AnyVal = _opacity
    override var visualizationSize: AnyVal = _visualizationSize

    override def setProps(props: PropsJsTrait): PropsJsTrait = {
      this.color = props.color
      this.visualizationSize = props.visualizationSize
      this.opacity = props.opacity
      this
    }
  }
}

abstract trait PropsJsTrait {
  var color: Color
  var opacity: AnyVal
  var visualizationSize: AnyVal

  def setProps(props: PropsJsTrait): PropsJsTrait

}

