package tmt.common

import org.scalajs.dom.URL

import scala.concurrent.duration.DurationInt
import scala.scalajs.js

object Constants {
  val CanvasWidth = 192*3
  val CanvasHeight = 108*3
  val URL = js.Dynamic.global.window.URL.asInstanceOf[URL]
  val RefreshRate = 5.seconds
}
