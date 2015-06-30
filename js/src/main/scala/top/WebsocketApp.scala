package top

import monifu.reactive.Observable
import top.common.RealImage
import org.scalajs.dom._
import org.scalajs.dom.html.{Image, Button, Canvas}
import org.scalajs.dom.raw.Event

import scala.Predef
import scala.concurrent.duration._
import scala.scalajs.js
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.typedarray.ArrayBuffer
import monifu.concurrent.Implicits.globalScheduler

import scala.scalajs.niocharset.StandardCharsets

object WebsocketApp extends JSApp {

  @JSExport
  override def main() = {
    val socket = new WebSocket(Constants.endpoint)
    socket.binaryType = "arraybuffer"
    socket.onopen = { e: Event =>
      Constants.button.onclick = { e: MouseEvent =>
        socket.send("join")
      }
    }

    Constants.canvas.width = window.innerWidth

    Factories.socketStream(socket)
      .map(Datum.fromMessage)
      .asyncBoundary()
      .zip(Observable.interval(10.millis))
      .foreach { case (datum, _) =>
        datum.render()
      }
  }
}

class CustomImage extends html.Image {
  var onload: js.Function0[ _] = js.native
}

case class Datum(url: String, image: RealImage) {
  val img = document.createElement("img").asInstanceOf[CustomImage]
  def render() = {
    img.onload = { () =>
      console.log("image loaded")
      Constants.ctx.drawImage(img, 0, 0, image.width/2, image.height/2)
      Constants.URL.revokeObjectURL(img.src)
    }
    img.src = url
  }
}

object Datum {
  def fromMessage(message: MessageEvent) = {
    val arrayBuffer = message.data.asInstanceOf[ArrayBuffer]
    val image = RealImageConversions.fromArrayBuffer(arrayBuffer)
    Constants.canvas.height = image.scaledHeight(Constants.canvas.width)
    val blob = RealImageConversions.toBlob(image)
    val url = Constants.URL.createObjectURL(blob)
    Datum(url, image)
  }
}

object Constants {
  val endpoint = "ws://localhost:6001/images"
  val button = document.getElementById("click-me-button").asInstanceOf[Button]
  val canvas = document.getElementById("canvas").asInstanceOf[Canvas]
  val URL = js.Dynamic.global.window.URL.asInstanceOf[URL]
  val ctx = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
  val img1 = document.getElementById("img1").asInstanceOf[Image]
}
