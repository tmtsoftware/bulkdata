package tmt.app

import org.scalajs.dom._
import tmt.common.models.PerSecMetric
import tmt.common.{CanvasControls, PerSecControls}
import tmt.images.ImageRendering
import tmt.metrics.MetricsRendering

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport

object WebsocketApp extends JSApp {

  @JSExport
  override def main() = {
    perSecMetrics()
    showImages()
  }

  def perSecMetrics(): Unit = {
    var socket: WebSocket = null
    PerSecControls.select.onchange = { e: Event =>
      if(socket != null) socket.close()
      socket = new WebSocket(PerSecControls.select.value)
      socket.binaryType = "arraybuffer"
      MetricsRendering.render[PerSecMetric](socket, PerSecControls.span)
    }
  }

  def showImages(): Unit = {
    var socket: WebSocket = null
    CanvasControls.select.onchange = { e: Event =>
      if(socket != null) socket.close()
      socket = new WebSocket(CanvasControls.select.value)
      socket.binaryType = "arraybuffer"
      ImageRendering.drain(socket)
    }
  }
}
