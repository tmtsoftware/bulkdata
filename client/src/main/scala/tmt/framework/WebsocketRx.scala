package tmt.framework

import org.scalajs.dom.raw.WebSocket
import rx._
import tmt.app.ViewData

abstract class WebsocketRx(viewData: ViewData) {
  val wsServer = Var("")
  val wsUrl = viewData.wsUrlOf(wsServer)
  val webSocket = Var(Option.empty[WebSocket])

  Obs(wsUrl) {
    webSocket().foreach(_.close())
    wsUrl().foreach { url =>
      val newSocket = new WebSocket(url)
      newSocket.binaryType = "arraybuffer"
      webSocket() = Some(newSocket)
    }
  }
}
