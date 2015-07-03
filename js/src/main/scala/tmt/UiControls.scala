package tmt

import org.scalajs.dom.html.{Button, Canvas}
import org.scalajs.dom.{CanvasRenderingContext2D, URL, document}
import tmt.common.Config

import scala.scalajs.js

object UiControls {
  val button = document.getElementById("click-me-button").asInstanceOf[Button]

  val canvas = document.getElementById("canvas").asInstanceOf[Canvas]
  canvas.width = Config.imageWidth
  canvas.height = Config.imageHeight

  val URL = js.Dynamic.global.window.URL.asInstanceOf[URL]
  val ctx = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
}
