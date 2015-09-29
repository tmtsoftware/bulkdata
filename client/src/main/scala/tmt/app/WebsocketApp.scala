package tmt.app

import boopickle.Default._
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.html.Select
import tmt.common.models.{PerSecMetric, RoleMappings}
import tmt.common.{CanvasControls, ImageRateControls, PerSecControls}
import tmt.images.ImageRendering
import tmt.metrics.MetricsRendering

import scala.async.Async._
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
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
    throttle()
    connect()
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

  def connect(): Unit = async {
    import tmt.common.SubscriptionControls._
    import upickle.default._

    import scalatags.JsDom.all._

    val roleMappings = read[RoleMappings](await(Ajax.get("/mappings")).responseText)

    var server1Select: Select = null
    var server2Select: Select = null

    role1.onchange = { e: Event =>
      val servers = roleMappings.getServers(role1.value)
      val selectNode = select(
        servers.map(s => option(value := s, s))
      ).render
      server1.innerHTML = ""
      server1.appendChild(selectNode)
      server1Select = selectNode
    }

    role2.onchange = { e: Event =>
      val servers = roleMappings.getServers(role2.value)
      val selectNode = select(
        servers.map(s => option(value := s, s))
      ).render
      server2.innerHTML = ""
      server2.appendChild(selectNode)
      server2Select = selectNode
    }

    button1.onclick = { e: Event =>
      val source = server1Select.value
      val target = server2Select.value
      Ajax.post(s"$target/subscribe/$source")
    }
  }
}
