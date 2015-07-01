package top

import org.scalajs.dom.document
import org.scalajs.dom.{CanvasRenderingContext2D, URL}
import org.scalajs.dom.html.{Button, Canvas, Image}

import scala.scalajs.js

object UiControls {
  val button = document.getElementById("click-me-button").asInstanceOf[Button]
  val canvas = document.getElementById("canvas").asInstanceOf[Canvas]
  val URL = js.Dynamic.global.window.URL.asInstanceOf[URL]
  val ctx = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
  val img1 = document.getElementById("img1").asInstanceOf[Image]
}
