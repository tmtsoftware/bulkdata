package tmt.app

import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.html.Select
import tmt.common.api.Api
import tmt.common.models.PerSecMetric
import tmt.common.{AjaxClient, ImageRateControls, CanvasControls, PerSecControls}
import tmt.images.ImageRendering
import tmt.metrics.MetricsRendering

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import autowire._
import async.Async._
import boopickle.Default._
import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow

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
    Ajax.get("/mappings").onComplete(x => println(x.get.responseText))
    AjaxClient[Api].getRoleMappings().call().onFailure {
      case ex => ex
    }
    dd()
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

  def dd(): Unit = {
    async {
      import scalatags.JsDom.all._

      println("aaa")
      val roleMappings = await(AjaxClient[Api].getRoleMappings().call())
      println(roleMappings)
      import tmt.common.SubscriptionControls._
      role1.onchange = { e: Event =>
        val servers = roleMappings.getServers(role1.value)
        val ee = select(
          servers.map(s => option(value := s, s)) :_*
        ).render
        server1.innerHTML = ""
        server1.add(ee)
      }


    }
  }

  def aa = {
    import scalatags.JsDom.all._

    img().render


  }
}
