package tmt.framework

import org.scalajs.dom.raw.WebSocket
import rx._
import rx.core.Rx
import tmt.app.ViewData
import Framework._

import scalatags.JsDom.all._

abstract class WebsocketRx(name: String, viewData: ViewData) extends FormRx(name, viewData) {

  def cleanup(): Unit
  
  def disconnect() = {
    webSocket().foreach(_.close())
    selectedServer() = name
    cleanup()
  }
  
  def getUrl = viewData.nodeSet().getWsUrl(server())

  val webSocket = Var(Option.empty[WebSocket])

  Obs(currentUrl, skipInitial = true) {
    webSocket().foreach(_.close())
    val newSocket = new WebSocket(currentUrl())
    newSocket.binaryType = "arraybuffer"
    webSocket() = Some(newSocket)
  }

  val disabledStyle = Rx(if(selectedServer() == name) "disabled" else "")

  val disconnectButton = a(cls := Rx(s"btn-floating waves-effect waves-light red ${disabledStyle()}"))(
    display := "inline-block", 
    float := "right",
    onclick := { () => disconnect() },
    i(cls := "material-icons")("remove")
  )
  
  val title = Rx {
    div(
      h5(display := "inline-block")(selectedServer().capitalize),
      disconnectButton
    )
  }
}
