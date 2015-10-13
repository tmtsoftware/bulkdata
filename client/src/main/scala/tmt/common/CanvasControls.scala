package tmt.common

import org.scalajs.dom.html._
import org.scalajs.dom.{CanvasRenderingContext2D, URL, document}
import tmt.shared.SharedConfigs

import scala.scalajs.js

object CanvasControls {
  val select = document.getElementById("source-selection").asInstanceOf[Select]
  val canvas = document.getElementById("canvas").asInstanceOf[Canvas]

  canvas.width = SharedConfigs.imageWidth
  canvas.height = SharedConfigs.imageHeight

  val URL = js.Dynamic.global.window.URL.asInstanceOf[URL]
  val ctx = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
}
