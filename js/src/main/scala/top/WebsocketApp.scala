package top

import java.nio.ByteBuffer

import org.scalajs.dom.raw.Event
import top.common.RealImage
import org.scalajs.dom._
import org.scalajs.dom.html.{Image, Button, Canvas}

import scala.scalajs.js
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.typedarray.{Uint8Array, ArrayBuffer, TypedArrayBuffer}
import js.JSConverters._

object WebsocketApp extends JSApp {

  val endpoint = "ws://localhost:6001/realimages"

  @JSExport
  override def main() = {
    val socket = new WebSocket(endpoint)
    val URL = js.Dynamic.global.window.URL.asInstanceOf[URL]
    val ctx = document.getElementById("canvas").asInstanceOf[Canvas].getContext("2d").asInstanceOf[CanvasRenderingContext2D]

    socket.binaryType = "arraybuffer"
    val playground = document.getElementById("playground")
    val abc = document.getElementById("abc")

    //    val img = Image.createBase64Svg()
    socket.onmessage = { e: MessageEvent =>
      val byteBuffer = TypedArrayBuffer.wrap(e.data.asInstanceOf[ArrayBuffer])
      val image = RealImage.fromBytes(byteBuffer)
      val ee = new Uint8Array(image.bytes.toJSArray)
      val dd = js.Dynamic.literal("type" -> image.encoding).asInstanceOf[BlobPropertyBag]
      val blob = new Blob(js.Array(ee), dd)
      val img = document.createElement("img").asInstanceOf[AA]
      val url = URL.createObjectURL(blob)
      println(url)
      img.onload = { () =>
        console.log("PNG Loaded")
        ctx.drawImage(img, 20, 20)
      }
      img.src = url
      playground.innerHTML = image.toString
    }

    socket.onopen = { e: Event =>
      val button = document.getElementById("click-me-button")
      button.asInstanceOf[Button].onclick = { e: MouseEvent =>
        socket.send("join")
//        xx()
      }
    }
  }

  def xx() = {
    val xhr = new XMLHttpRequest()
    xhr.open( "GET", "http://i.imgur.com/ig9IDfT.jpg", true)
    xhr.responseType = "arraybuffer"
    xhr.onload = { (e: Event) =>
      val arrayBuffer = xhr.response.asInstanceOf[ArrayBuffer]
      val byteBuffer = TypedArrayBuffer.wrap(arrayBuffer)
      val dst = Array.ofDim[Byte](arrayBuffer.byteLength)
      byteBuffer.get(dst)
//      byteBuffer.flip()
      val dd = DD(dst)
      val dd1 = DD.fromBytes(DD.toBytes(dd))
//      val ee = new Uint8Array(arrayBuffer)
      val ee = new Uint8Array(dd1.bytes.toJSArray)
//      val ee = dd.bytes.toJSArray.map(x => x: js.Any)
      val blob = new Blob(js.Array(ee))
      val URL = js.Dynamic.global.window.URL.asInstanceOf[URL]
      val imageUrl = URL.createObjectURL( blob )
      println(imageUrl)
      val img = document.getElementById("photo").asInstanceOf[Image]
      img.src = imageUrl
    }

    xhr.send()
  }

}

class AA extends html.Image {
  var onload: js.Function0[ _] = js.native
}

case class DD(bytes: Array[Byte]) {

}

object DD {
  import boopickle._

  def toBytes(image: DD): ByteBuffer = Pickle.intoBytes(image)
  def fromBytes(bytes: ByteBuffer) =  Unpickle[DD].fromBytes(bytes)
}
