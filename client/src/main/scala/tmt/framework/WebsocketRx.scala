package tmt.framework

import org.scalajs.dom.raw.WebSocket
import rx._
import tmt.app.ViewData

abstract class WebsocketRx(viewData: ViewData) {
  val wsServer = Var("")
  val wsUrl = Var("")
  val webSocket = Var(Option.empty[WebSocket])

  Obs(wsUrl, skipInitial = true) {
    webSocket().foreach(_.close())
    val newSocket = new WebSocket(wsUrl())
    newSocket.binaryType = "arraybuffer"
    webSocket() = Some(newSocket)
  }

  def setUrl() = {
    val maybeUrl = viewData.nodeSet().getHost(wsServer())
    maybeUrl.foreach { url =>
      wsUrl() = url
    }
  }

  Obs(viewData.diffs) {
    if (viewData.diffs() contains wsServer()) {
      setUrl()
    }
  }

}
