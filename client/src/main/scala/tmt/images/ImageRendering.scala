package tmt.images

import monifu.concurrent.Scheduler
import org.scalajs.dom._
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.Blob
import rx._
import tmt.common.{Constants, Stream}

import scala.concurrent.duration._
import scala.scalajs.js
import scala.scalajs.js.typedarray.ArrayBuffer

class ImageRendering(implicit scheduler: Scheduler) {

  val imageNode = Var("")
  val imageSocket = Var(Option.empty[WebSocket])

  Obs(imageNode, skipInitial = true) {
    imageSocket().foreach(_.close())
    val node = imageNode()
    val newSocket = new WebSocket(node)
    newSocket.binaryType = "arraybuffer"
    imageSocket() = Some(newSocket)
  }

  def drawOn(cvs: Canvas) = Obs(imageSocket) {
    imageSocket().foreach(socket => drain(socket, cvs))
  }
  
  def drain(socket: WebSocket, canvas: Canvas) = Stream.socket(socket)
    .map(makeUrl)
    .map(new Rendering(_))
    .flatMap(_.loaded)
    .map(_.render(canvas))
    .buffer(1.second).map(_.size).foreach(println)

  def makeUrl(messageEvent: MessageEvent) = {
    val arrayBuffer = messageEvent.data.asInstanceOf[ArrayBuffer]
    val properties = js.Dynamic.literal("type" -> "image/jpeg").asInstanceOf[BlobPropertyBag]
    val blob = new Blob(js.Array(arrayBuffer), properties)
    Constants.URL.createObjectURL(blob)
  }
}
