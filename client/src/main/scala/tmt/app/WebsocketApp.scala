package tmt.app

import org.scalajs.dom._
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.html._
import tmt.common.CanvasControls
import tmt.images.ImageRendering
import tmt.shared.models.{ConnectionSet, HostMappings, RoleIndex}
import tmt.views.{StreamView, SubscriptionView, ThrottleView}
import prickle._

import scala.async.Async._
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport

object WebsocketApp extends JSApp {

  @JSExport
  override def main() = async {
    val roleIndex = Unpickle[RoleIndex].fromString(await(Ajax.get("/mappings")).responseText).get
    val connectionSet = Unpickle[ConnectionSet].fromString(await(Ajax.get("/connections")).responseText).get
    val hostMappings = Unpickle[HostMappings].fromString(await(Ajax.get("/hosts")).responseText).get

    val subscriptionDiv = new SubscriptionView(roleIndex, connectionSet).frag.render
    val streamDiv = new StreamView(roleIndex, hostMappings).frag.render
    val throttleDiv = new ThrottleView(roleIndex).frag.render

    document.body.appendChild(throttleDiv)
    document.body.appendChild(subscriptionDiv)
    document.body.appendChild(streamDiv)

    handleSelection(CanvasControls.select1, CanvasControls.canvas1)
    handleSelection(CanvasControls.select2, CanvasControls.canvas2)

  }

  private def handleSelection(select: Select, canvas: Canvas) = {
    render(select) { socket =>
      ImageRendering.drain(socket, canvas)
    }
  }

  private def render(select: Select)(block: WebSocket => Unit): Unit = {
    var socket: WebSocket = null
    select.onchange = { e: Event =>
      if (socket != null) socket.close()
      socket = new WebSocket(select.value)
      socket.binaryType = "arraybuffer"
      block(socket)
    }
  }
}
