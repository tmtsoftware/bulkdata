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
  override def main() = {
    showImages()
    cumulativeMetrics()
    perSecMetrics()
  }

  @JSExport
  def showImages(): Unit = {
    CanvasControls.button.onclick = { e: Event =>
      val socket = new WebSocket(s"ws://image.source:8000/images/bytes")
      socket.binaryType = "arraybuffer"
      ImageRendering.drain(socket)
    }
  }

  @JSExport
  def perSecMetrics(): Unit = {
    PerSecControls.button.onclick = { e: Event =>
      val socket = new WebSocket(s"ws://metrics.agg:8005/metrics-per-sec")
      socket.binaryType = "arraybuffer"
      MetricsRendering.render[PerSecMetric](socket, PerSecControls.span)
    }
  }

  @JSExport
  def cumulativeMetrics(): Unit = {
    CumulativeControls.button.onclick = { e: Event =>
      val socket = new WebSocket(s"ws://metrics.agg:8005/metrics-cumulative")
      socket.binaryType = "arraybuffer"
      MetricsRendering.render[CumulativeMetric](socket, CumulativeControls.span)
    }
  }
}
