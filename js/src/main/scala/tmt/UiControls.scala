package tmt

import org.scalajs.dom.html.{Button, Canvas}
import org.scalajs.dom.{CanvasRenderingContext2D, URL, document}

import scala.scalajs.js

object UiControls {
  val button = document.getElementById("click-me-button").asInstanceOf[Button]
  val canvas = document.getElementById("canvas").asInstanceOf[Canvas]
  canvas.width = 1024
  canvas.height = 1024

  val URL = js.Dynamic.global.window.URL.asInstanceOf[URL]
  val ctx = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
}
