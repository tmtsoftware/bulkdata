package tmt.app

import boopickle.Default._
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.html._
import tmt.common.{CanvasControls, ImageRateControls, PerSecControls}
import tmt.images.ImageRendering
import tmt.metrics.MetricsRendering
import tmt.shared.models.{HostMappings, ConnectionSet, PerSecMetric, RoleMappings}
import tmt.views.{StreamView, SubscriptionView}
import upickle.default._

import scala.async.Async._
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport

object WebsocketApp extends JSApp {

  @JSExport
  override def main() = async {
    val roleMappings = read[RoleMappings](await(Ajax.get("/mappings")).responseText)
    val connectionSet = read[ConnectionSet](await(Ajax.get("/connections")).responseText)
    val hostMappings = read[HostMappings](await(Ajax.get("/hosts")).responseText)
    val subscriptionDiv = new SubscriptionView(roleMappings, connectionSet).frag.render
    val streamDiv = new StreamView(roleMappings, hostMappings).frag.render
    document.body.appendChild(subscriptionDiv)
    document.body.appendChild(streamDiv)

    render(PerSecControls.select) { socket =>
      MetricsRendering.render[PerSecMetric](socket, PerSecControls.span)
    }
    render(CanvasControls.select) { socket =>
      ImageRendering.drain(socket)
    }
    throttle()
  }

  def render(select: Select)(block: WebSocket => Unit): Unit = {
    var socket: WebSocket = null
    select.onchange = { e: Event =>
      if (socket != null) socket.close()
      socket = new WebSocket(select.value)
      socket.binaryType = "arraybuffer"
      block(socket)
    }
  }

  def throttle(): Unit = {
    import dom.ext._

    import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow

    ImageRateControls.button.onclick = { e: Event =>
      val url = s"${ImageRateControls.serverName.value}/throttle/${ImageRateControls.newRate.value}"

      Ajax.post(url).onSuccess { case xhr =>
        println("success")
      }
    }
  }
}
