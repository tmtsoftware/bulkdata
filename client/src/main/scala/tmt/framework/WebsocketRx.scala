package tmt.framework

import org.scalajs.dom.raw.WebSocket
import rx._
import tmt.app.ViewData

class WebsocketRx(viewData: ViewData) extends FormRx(viewData) {

  def getUrl = viewData.nodeSet().getWsUrl(server())

  val webSocket = Var(Option.empty[WebSocket])

  Obs(currentUrl, skipInitial = true) {
    webSocket().foreach(_.close())
    val newSocket = new WebSocket(currentUrl())
    newSocket.binaryType = "arraybuffer"
    webSocket() = Some(newSocket)
  }
}
