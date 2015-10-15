package tmt.images

import org.scalajs.dom._
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.raw.Blob
import tmt.common.{Stream, CanvasControls}

import scala.concurrent.duration._
import scala.scalajs.js
import scala.scalajs.js.typedarray.ArrayBuffer
import monifu.concurrent.Implicits.globalScheduler

object ImageRendering {
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
    CanvasControls.URL.createObjectURL(blob)
  }
}
