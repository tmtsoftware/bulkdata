package top

import monifu.reactive.Observable
import org.scalajs.dom._

import scala.concurrent.duration._
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import monifu.concurrent.Implicits.globalScheduler

object WebsocketApp extends JSApp {

  @JSExport
  override def main() = {
    UiControls.button.onclick = { e: Event =>
      val socket = new WebSocket("ws://localhost:6001/images")
      socket.binaryType = "arraybuffer"
      Stream.from(socket)
        .map(RenderingData.fromMessage)
        .buffer(500).concatMap(Observable.fromIterable)
        .zip(Observable.interval(24.millis)).map(_._1)
        .foreach(_.render())
    }
  }
}
