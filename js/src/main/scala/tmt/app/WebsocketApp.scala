package tmt.app

import org.scalajs.dom._
import tmt.common.models.{CumulativeMetric, PerSecMetric}
import tmt.common.{Hosts, CanvasControls, CumulativeControls, PerSecControls}
import tmt.images.ImageRendering
import tmt.metrics.MetricsRendering

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport

object WebsocketApp extends JSApp {

  @JSExport
  override def main() = {
    showImages("dev")
    cumulativeMetrics("dev")
    perSecMetrics("dev")
  }

  @JSExport
  def showImages(env: String): Unit = {
    CanvasControls.button.onclick = { e: Event =>
      val socket = new WebSocket(s"ws://${Hosts.getHost(env, "hosts.image-source")}:8000/images/bytes")
      socket.binaryType = "arraybuffer"
      ImageRendering.drain(socket)
    }
  }

  @JSExport
  def perSecMetrics(env: String): Unit = {
    PerSecControls.button.onclick = { e: Event =>
      val socket = new WebSocket(s"ws://${Hosts.getHost(env, "hosts.metrics-agg")}:8005/metrics-per-sec")
      socket.binaryType = "arraybuffer"
      MetricsRendering.render[PerSecMetric](socket, PerSecControls.span)
    }
  }

  @JSExport
  def cumulativeMetrics(env: String): Unit = {
    CumulativeControls.button.onclick = { e: Event =>
      val socket = new WebSocket(s"ws://${Hosts.getHost(env, "hosts.metrics-agg")}:8005/metrics-cumulative")
      socket.binaryType = "arraybuffer"
      MetricsRendering.render[CumulativeMetric](socket, CumulativeControls.span)
    }
  }
}
