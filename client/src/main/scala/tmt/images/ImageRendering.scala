package tmt.images

import monifu.concurrent.Scheduler
import org.scalajs.dom._
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.Blob
import rx._
import tmt.app.ViewData
import tmt.common.{Constants, Stream}
import tmt.framework.WebsocketRx

import scala.concurrent.duration._
import scala.scalajs.js
import scala.scalajs.js.typedarray.ArrayBuffer

class ImageRendering(cvs: Canvas, viewData: ViewData)(implicit scheduler: Scheduler) extends WebsocketRx("Wavefront", viewData) {


  def cleanup() = {
    ctx.clearRect(0, 0, Constants.CanvasWidth, Constants.CanvasHeight)
  }

  Rx(webSocket().foreach(drain))

  val ctx = cvs.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

  def drain(socket: WebSocket) = Stream.socket(socket)
    .map(makeUrl)
    .map(new Rendering(_))
    .flatMap(_.loaded)
    .map(_.render(ctx))
    .buffer(1.second).map(_.size).foreach(println)

  def makeUrl(messageEvent: MessageEvent) = {
    val arrayBuffer = messageEvent.data.asInstanceOf[ArrayBuffer]
    val properties = js.Dynamic.literal("type" -> "image/jpeg").asInstanceOf[BlobPropertyBag]
    val blob = new Blob(js.Array(arrayBuffer), properties)
    Constants.URL.createObjectURL(blob)
  }
}
