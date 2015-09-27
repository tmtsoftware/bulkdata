package tmt.app

import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.html.Select
import tmt.common.models.PerSecMetric
import tmt.common.{ImageRateControls, CanvasControls, PerSecControls}
import tmt.images.ImageRendering
import tmt.metrics.MetricsRendering

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport

object WebsocketApp extends JSApp {

  @JSExport
  override def main() = {
    render(PerSecControls.select) { socket =>
      MetricsRendering.render[PerSecMetric](socket, PerSecControls.span)
    }
    render(CanvasControls.select) { socket =>
      ImageRendering.drain(socket)
    }
    changeImageRate()
  }

  def render(select: Select)(block: WebSocket => Unit): Unit = {
    var socket: WebSocket = null
    select.onchange = { e: Event =>
      if(socket != null) socket.close()
      socket = new WebSocket(select.value)
      socket.binaryType = "arraybuffer"
      block(socket)
    }
  }

  def changeImageRate(): Unit = {
    import dom.ext._
    import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow

    ImageRateControls.button.onclick = { e: Event =>
      val url = s"${ImageRateControls.serverName.value}/throttle/${ImageRateControls.newRate.value}"

      Ajax.post(url).onSuccess{ case xhr =>
          println("success")
      }
    }
  }
}
