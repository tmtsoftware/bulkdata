package tmt.common

import org.scalajs.dom.html.{Button, Canvas}
import org.scalajs.dom.{CanvasRenderingContext2D, URL, document}

import scala.scalajs.js

object UiControls {
  val button = document.getElementById("click-me-button").asInstanceOf[Button]

  val canvas = document.getElementById("canvas").asInstanceOf[Canvas]
  canvas.width = SharedConfigs.imageWidth
  canvas.height = SharedConfigs.imageHeight

  val URL = js.Dynamic.global.window.URL.asInstanceOf[URL]
  val ctx = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
}
