package tmt.common

import org.scalajs.dom.html._
import org.scalajs.dom.{CanvasRenderingContext2D, URL, document}
import tmt.shared.SharedConfigs

import scala.scalajs.js

object CanvasControls {
  val select1 = document.getElementById("source-selection1").asInstanceOf[Select]
  val select2 = document.getElementById("source-selection2").asInstanceOf[Select]
  
  val canvas1 = document.getElementById("canvas1").asInstanceOf[Canvas]
  val canvas2 = document.getElementById("canvas2").asInstanceOf[Canvas]

  canvas1.width = SharedConfigs.imageWidth
  canvas1.height = SharedConfigs.imageHeight

  canvas2.width = SharedConfigs.imageWidth
  canvas2.height = SharedConfigs.imageHeight

  val URL = js.Dynamic.global.window.URL.asInstanceOf[URL]
}
