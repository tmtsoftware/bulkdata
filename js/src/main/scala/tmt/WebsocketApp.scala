package tmt

import monifu.concurrent.Implicits.globalScheduler
import org.scalajs.dom._
import tmt.common.Config

import scala.concurrent.duration._
import scala.scalajs.js
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.typedarray.ArrayBuffer

object WebsocketApp extends JSApp {

  @JSExport
  override def main() = UiControls.button.onclick = { e: Event =>
    val socket = new WebSocket(s"ws://${Config.interface}:${Config.port}/images")
    socket.binaryType = "arraybuffer"
    Stream.from(socket).asyncBoundary()
      .map(makeUrl)
      .map(new Rendering(_))
      .flatMap(_.loadedRendering)
      .map(_.render())
      .bufferTimed(1.second).map(_.size).foreach(println)
  }

  def makeUrl(messageEvent: MessageEvent) = {
    val arrayBuffer = messageEvent.data.asInstanceOf[ArrayBuffer]
    val properties = js.Dynamic.literal("type" -> "image/jpeg").asInstanceOf[BlobPropertyBag]
    val blob = new Blob(js.Array(arrayBuffer), properties)
    UiControls.URL.createObjectURL(blob)
  }
}
