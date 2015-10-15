package tmt.common

import org.scalajs.dom.html._
import org.scalajs.dom.{URL, document}

import scala.scalajs.js

object CanvasControls {
  val select1 = document.getElementById("source-selection1").asInstanceOf[Select]
  val select2 = document.getElementById("source-selection2").asInstanceOf[Select]
  
  val canvas1 = document.getElementById("canvas1").asInstanceOf[Canvas]
  val canvas2 = document.getElementById("canvas2").asInstanceOf[Canvas]

  canvas1.width = Constants.canvasWidth
  canvas1.height = Constants.canvasHeight

  canvas2.width = Constants.canvasWidth
  canvas2.height = Constants.canvasHeight

  val URL = js.Dynamic.global.window.URL.asInstanceOf[URL]
}
