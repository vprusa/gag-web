/*
Copyright (c) 2018 Vojtěch Průša
*/
package cz.muni.fi.gag.web.scala.shared.common

object Gesture {
  val allowedMatch = 0.9f
}

trait Gesture {
  /**
   * This method consumes data and returns statistical match <0,1>
   *
   * Also this method consumes HashMap<Date, Quaternion> parsed of Date and
   * quaternions
   *
   * @param data
   * @return
   */
  def matchesBy(data: Nothing): Float

  /**
   * If matches gesture
   *
   * @param data
   * @return
   */
  def matches(data: Nothing): Boolean = matchesBy(data) > getAllowedMatch

  def getAllowedMatch(): Float = Gesture.allowedMatch
}