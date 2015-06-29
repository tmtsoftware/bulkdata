package top

import top.common.RealImage
import org.scalajs.dom._
import org.scalajs.dom.html.{Image, Button, Canvas}

import scala.scalajs.js
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.typedarray.{ArrayBuffer, TypedArrayBuffer}

object WebsocketApp extends JSApp {

  val endpoint = "ws://localhost:6001/realimages"

  @JSExport
  override def main() = {
    val socket = new WebSocket(endpoint)
    val URL = js.Dynamic.global.window.URL.asInstanceOf[URL]
    val ctx = document.getElementById("canvas").asInstanceOf[Canvas].getContext("2d").asInstanceOf[CanvasRenderingContext2D]

    socket.binaryType = "arraybuffer"
    val playground = document.getElementById("playground")
//    val img = Image.createBase64Svg()
    socket.onmessage = { e: MessageEvent =>
      val buffer = TypedArrayBuffer.wrap(e.data.asInstanceOf[ArrayBuffer])
      val image = RealImage.fromBytes(buffer)
      val blob = new Blob(js.Array(js.Array(image.bytes: _*)))
      val img = document.createElement("img").asInstanceOf[Image]
      img.onloadeddata = { e : Event =>
        println("PNG Loaded")
        ctx.drawImage(img, 20, 20)
      }
      img.src = URL.createObjectURL(blob)
      playground.innerHTML = image.toString
    }

    socket.onopen = { e: Event =>
      val button = document.getElementById("click-me-button")
      button.asInstanceOf[Button].onclick = { e: MouseEvent =>
        socket.send("join")
      }
    }
  }

}
