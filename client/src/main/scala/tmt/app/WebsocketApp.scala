package tmt.app

import boopickle.Default._
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.html.{Button, Div, LI, Select}
import tmt.common.{CanvasControls, ImageRateControls, PerSecControls}
import tmt.images.ImageRendering
import tmt.metrics.MetricsRendering
import tmt.shared.models.{PerSecMetric, RoleMappings}

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

    def populateOptions(role: Select, server: Div) = role.onchange = { e: Event =>
      val servers = roleMappings.getServers(role.value)
      val selectNode = select(id := s"${server.id}-select")(
        option(selected := true, disabled, hidden := true, value := "")("select-server"),
        servers.map(s => option(value := s, s))
      ).render
      server.innerHTML = ""
      server.appendChild(selectNode)
    }

    populateOptions(role1, server1)
    populateOptions(role2, server2)

    button1.onclick = { e: Event =>
      val topic = server1Select().value
      val serverName = server2Select().value
      Ajax.post(s"$serverName/subscribe/$topic")
      val buttonNode = button(data := s"/$serverName/unsubscribe/$topic")("unsubscribe").render
      val liNode = li(s"$serverName is subscribed to $topic", buttonNode).render
      removeLi(liNode, buttonNode)
      ul1.appendChild(liNode)
    }

    def addUnsubscribeCallback() = (0 until lis.length).foreach { index =>
      val liNode = lis.item(index).asInstanceOf[LI]
      val buttonNode = liNode.getElementsByTagName("button")(0).asInstanceOf[Button]
      removeLi(liNode, buttonNode)
    }

    def removeLi(liNode: LI, buttonNode: Button) = buttonNode.onclick = { e: Event =>
      Ajax.post(s"${buttonNode.getAttribute("data")}")
      ul1.removeChild(liNode)
    }

    addUnsubscribeCallback()
  }
}
