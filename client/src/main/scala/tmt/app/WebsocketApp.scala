package tmt.app

import org.scalajs.dom._
import tmt.common.models.{CumulativeMetric, PerSecMetric}
import tmt.common.{CanvasControls, CumulativeControls, PerSecControls}
import tmt.images.ImageRendering
import tmt.metrics.MetricsRendering

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport

object WebsocketApp extends JSApp {

  @JSExport
  override def main() = ()

  @JSExport
  def showImages(url: String): Unit = {
    CanvasControls.button.onclick = { e: Event =>
      val socket = new WebSocket(url)
      socket.binaryType = "arraybuffer"
      ImageRendering.drain(socket)
    }
  }

  @JSExport
  def perSecMetrics(url: String): Unit = {
    PerSecControls.button.onclick = { e: Event =>
      val socket = new WebSocket(url)
      socket.binaryType = "arraybuffer"
      MetricsRendering.render[PerSecMetric](socket, PerSecControls.span)
    }
  }

  @JSExport
  def cumulativeMetrics(url: String): Unit = {
    CumulativeControls.button.onclick = { e: Event =>
      val socket = new WebSocket(url)
      socket.binaryType = "arraybuffer"
      MetricsRendering.render[CumulativeMetric](socket, CumulativeControls.span)
    }
  }
}
