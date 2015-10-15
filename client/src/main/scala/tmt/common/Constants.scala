package tmt.common

import org.scalajs.dom.URL

import scala.scalajs.js

object Constants {
  val canvasWidth = 192*4
  val canvasHeight = 108*4
  val URL = js.Dynamic.global.window.URL.asInstanceOf[URL]
}
