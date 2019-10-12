/*
Copyright (c) 2018 Vojtěch Průša
*/
package cz.muni.fi.gag.web.common.visualization

import cz.muni.fi.gag.web.common.Configuration

/**
 * @author Vojtech Prusa
 *
 *         Used in ogl to draw 3D line
 */
class Line3D private[visualization](var begin: Point3D, var end: Point3D) {
  private[visualization] def draw(): Unit = {
    if (Configuration.app == null) return
    Configuration.app._line(begin.x, begin.y, begin.z, end.x, end.y, end.z)
  }
}