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

    def populateOptions(selectRole: Select, serverDiv: Div) = selectRole.onchange = { e: Event =>
      val servers = roleMappings.getServers(selectRole.value)
      val selectNode = select(id := s"${serverDiv.id}-select")(
        option(selected := true, disabled, hidden := true, value := "")("select-server"),
        servers.map(s => option(value := s, s))
      ).render
      serverDiv.innerHTML = ""
      serverDiv.appendChild(selectNode)
    }

    populateOptions(selectRole1, divServer1)
    populateOptions(selectRole2, divServer2)

    subscribeButton.onclick = { e: Event =>
      val topic = selectServer1().value
      val serverName = selectServer2().value
      Ajax.post(s"$serverName/subscribe/$topic")
      val buttonNode = button(data := s"/$serverName/unsubscribe/$topic")("unsubscribe").render
      val liNode = li(s"$serverName is subscribed to $topic", buttonNode).render
      removeLi(liNode, buttonNode)
      connectionsUl.appendChild(liNode)
    }

    def addUnsubscribeCallback() = (0 until connectionsLis.length).foreach { index =>
      val liNode = connectionsLis.item(index).asInstanceOf[LI]
      val buttonNode = liNode.getElementsByTagName("button")(0).asInstanceOf[Button]
      removeLi(liNode, buttonNode)
    }

    def removeLi(liNode: LI, buttonNode: Button) = buttonNode.onclick = { e: Event =>
      Ajax.post(s"${buttonNode.getAttribute("data")}")
      connectionsUl.removeChild(liNode)
    }

    addUnsubscribeCallback()
  }
}
