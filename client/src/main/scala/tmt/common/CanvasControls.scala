package tmt.common

import org.scalajs.dom.html._
import org.scalajs.dom.{CanvasRenderingContext2D, URL, document}

import scala.scalajs.js
import Helper._

object CanvasControls {
  val select = document.getElementById("source-selection").asInstanceOf[Select]
  val canvas = document.getElementById("canvas").asInstanceOf[Canvas]

  canvas.width = SharedConfigs.imageWidth
  canvas.height = SharedConfigs.imageHeight

  val URL = js.Dynamic.global.window.URL.asInstanceOf[URL]
  val ctx = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
}

object PerSecControls {
  val select = document.getElementById("frequency-selection").asInstanceOf[Select]
  val span = document.getElementById("per-sec").asInstanceOf[Span]
}

object ImageRateControls {
  val button = document.getElementById("change-rate").asInstanceOf[Button]
  val serverName = document.getElementById("server-name").asInstanceOf[Select]
  val newRate = document.getElementById("new-rate").asInstanceOf[Input]
}

object SubscriptionControls {
  val role1 = document.get[Select]("role1")
  val role2 = document.get[Select]("role2")

  val server1 = document.get[Div]("server1")
  val server2 = document.get[Div]("server2")

  val server1Select = document.get[Select](s"${server1.id}-select")
  val server2Select = document.get[Select](s"${server2.id}-select")

  val button1 = document.get[Button]("subscribe")
}

object Helper {
  implicit class RichDoc(val doc: Document) extends AnyVal {
    def get[T](id: String) = doc.getElementById(id).asInstanceOf[T]
  }
}
