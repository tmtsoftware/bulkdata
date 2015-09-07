package tmt.common

import org.scalajs.dom.html.{Span, Button, Canvas}
import org.scalajs.dom.{CanvasRenderingContext2D, URL, document}

import scala.scalajs.js

object CanvasControls {
  val button = document.getElementById("show-images").asInstanceOf[Button]
  val canvas = document.getElementById("canvas").asInstanceOf[Canvas]

  canvas.width = SharedConfigs.imageWidth
  canvas.height = SharedConfigs.imageHeight

  val URL = js.Dynamic.global.window.URL.asInstanceOf[URL]
  val ctx = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
}

object CumulativeControls {
  val button = document.getElementById("show-cumulative").asInstanceOf[Button]
  val span = document.getElementById("cumulative").asInstanceOf[Span]
}

object PerSecControls {
  val button = document.getElementById("show-per-sec").asInstanceOf[Button]
  val span = document.getElementById("per-sec").asInstanceOf[Span]
}
