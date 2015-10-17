package tmt.framework

import org.scalajs.dom.raw.WebSocket
import rx._

trait WebsocketRx {
  val wsUrl = Var("")
  val webSocket = Var(Option.empty[WebSocket])
  Obs(wsUrl, skipInitial = true) {
    webSocket().foreach(_.close())
    val node = wsUrl()
    val newSocket = new WebSocket(node)
    newSocket.binaryType = "arraybuffer"
    webSocket() = Some(newSocket)
  }
}
