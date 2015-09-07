package tmt.app

import org.scalajs.dom._
import tmt.common.models.{PerSecMetric, CumulativeMetric}
import tmt.common.{PerSecControls, CumulativeControls, CanvasControls, SharedConfigs}
import tmt.images.ImageRendering
import tmt.metrics.MetricsRendering

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport

object WebsocketApp extends JSApp {

  @JSExport
  override def main() = {
    CanvasControls.button.onclick = { e: Event =>
      val socket = new WebSocket(s"ws://${SharedConfigs.interface}:${SharedConfigs.port}/images/bytes")
      socket.binaryType = "arraybuffer"
      ImageRendering.drain(socket)
    }

    CumulativeControls.button.onclick = { e: Event =>
      val socket = new WebSocket(s"ws://${SharedConfigs.interface}:8005/metrics-cumulative")
      socket.binaryType = "arraybuffer"
      MetricsRendering.render[CumulativeMetric](socket, CumulativeControls.span)
    }

    PerSecControls.button.onclick = { e: Event =>
      val socket = new WebSocket(s"ws://${SharedConfigs.interface}:8005/metrics-per-sec")
      socket.binaryType = "arraybuffer"
      MetricsRendering.render[PerSecMetric](socket, PerSecControls.span)
    }
  }
}
