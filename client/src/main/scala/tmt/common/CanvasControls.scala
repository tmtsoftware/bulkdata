package tmt.common

import org.scalajs.dom.html._
import org.scalajs.dom.{CanvasRenderingContext2D, URL, document}
import tmt.shared.SharedConfigs

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
  val selectRole1 = document.get[Select]("role1")
  val selectRole2 = document.get[Select]("role2")

  val divServer1 = document.get[Div]("server1")
  val divServer2 = document.get[Div]("server2")

  def selectServer1() = document.get[Select](s"${divServer1.id}-select")
  def selectServer2() = document.get[Select](s"${divServer2.id}-select")

  val subscribeButton = document.get[Button]("subscribe")

  val connectionsUl = document.get[UList]("connections")
  val connectionsLis = connectionsUl.getElementsByTagName("li")
}

object Helper {
  implicit class RichDoc(val doc: Document) extends AnyVal {
    def get[T](id: String) = doc.getElementById(id).asInstanceOf[T]
  }
}
