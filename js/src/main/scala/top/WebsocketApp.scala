package top

import org.scalajs.dom._
import org.scalajs.dom.html.{Button, Canvas}
import org.scalajs.dom.raw.Event

import scala.scalajs.js
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.typedarray.ArrayBuffer

object WebsocketApp extends JSApp {

  val endpoint = "ws://localhost:6001/images"
  val URL = js.Dynamic.global.window.URL.asInstanceOf[URL]
  val canvas = document.getElementById("canvas").asInstanceOf[Canvas]
  val ctx = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
  val button = document.getElementById("click-me-button").asInstanceOf[Button]
  val img = document.createElement("img").asInstanceOf[CustomImage]

  @JSExport
  override def main() = {
    val socket = new WebSocket(endpoint)
    socket.binaryType = "arraybuffer"

    socket.onmessage = { message: MessageEvent =>
      val arrayBuffer = message.data.asInstanceOf[ArrayBuffer]
      val image = RealImageConversions.fromArrayBuffer(arrayBuffer)
      canvas.width = window.innerWidth
      canvas.height = image.scaledHeight(canvas.width)
      val blob = RealImageConversions.toBlob(image)
      val url = URL.createObjectURL(blob)
      img.onload = { () =>
        console.log("image loaded")
        ctx.drawImage(img, 0, 0, image.width/2, image.height/2)
        URL.revokeObjectURL(img.src)
      }
      img.src = url
    }

    socket.onopen = { e: Event =>
      button.onclick = { e: MouseEvent =>
        socket.send("join")
      }
    }
  }
}

class CustomImage extends html.Image {
  var onload: js.Function0[ _] = js.native
}
