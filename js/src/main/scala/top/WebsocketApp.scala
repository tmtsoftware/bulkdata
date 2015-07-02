package top

import boopickle.Unpickle
import monifu.concurrent.Implicits.globalScheduler
import monifu.reactive.Observable
import top.StreamExtensions.RichStream
import top.common.{Image, Config}
import org.scalajs.dom._

import scala.scalajs.js
import scala.scalajs.js.JSApp
import scala.scalajs.js.JSConverters.array2JSRichGenTrav
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.typedarray.{Uint8Array, TypedArrayBuffer, ArrayBuffer}

object WebsocketApp extends JSApp {

  @JSExport
  override def main() = UiControls.button.onclick = { e: Event =>
    val socket = new WebSocket(s"ws://${Config.interface}:${Config.port}/images")
    socket.binaryType = "arraybuffer"
    createRenderings(socket).foreach(_.render())
  }

  def createRenderings(socket: WebSocket) = Stream.from(socket)
    .map(_.data.asInstanceOf[ArrayBuffer])
    .map(TypedArrayBuffer.wrap)
    .map(Unpickle[Image].fromBytes)
    .map(x => x -> x.bytes.toJSArray)
    .mapValues(new Uint8Array(_))
    .map { case (image, array) => image -> new Blob(js.Array(array), imageProperties(image)) }
    .mapValues(UiControls.URL.createObjectURL(_))
    .map { case (image, url) => new Rendering(url, image.width / 2, image.height / 2) }

  def imageProperties(image: Image) =
    js.Dynamic.literal("type" -> image.mimeType).asInstanceOf[BlobPropertyBag]

}
