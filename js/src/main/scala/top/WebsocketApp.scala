package top

import boopickle.Unpickle
import monifu.concurrent.Implicits.globalScheduler
import top.common.{Config, Image}
import org.scalajs.dom._

import scala.scalajs.js
import scala.scalajs.js.JSApp
import scala.scalajs.js.JSConverters.array2JSRichGenTrav
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.typedarray.{ArrayBuffer, TypedArrayBuffer, Uint8Array}

object WebsocketApp extends JSApp {

  @JSExport
  override def main() = UiControls.button.onclick = { e: Event =>
    val socket = new WebSocket(s"ws://${Config.interface}:${Config.port}/images")
    socket.binaryType = "arraybuffer"
    Stream
      .from(socket)
      .map(makeImage)
      .map(makeRendering)
      .foreach(_.render())
  }

  def makeImage(messageEvent: MessageEvent) = {
    val arrayBuffer = messageEvent.data.asInstanceOf[ArrayBuffer]
    val byteBuffer = TypedArrayBuffer.wrap(arrayBuffer)
    Unpickle[Image].fromBytes(byteBuffer)
  }

  def makeRendering(image: Image) = {
    val imageBytes = new Uint8Array(image.bytes.toJSArray)
    val properties = js.Dynamic.literal("type" -> image.mimeType).asInstanceOf[BlobPropertyBag]
    val blob = new Blob(js.Array(imageBytes), properties)
    val url = UiControls.URL.createObjectURL(blob)
    new Rendering(url, image.width / 2, image.height / 2)
  }
}
