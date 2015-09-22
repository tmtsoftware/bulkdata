package tmt.images

import org.scalajs.dom._
import org.scalajs.dom.raw.Blob
import tmt.common.{CanvasControls, Stream}

import scala.concurrent.duration._
import scala.scalajs.js
import scala.scalajs.js.typedarray.ArrayBuffer
import monifu.concurrent.Implicits.globalScheduler

object ImageRendering {
  def drain(socket: WebSocket) = Stream.socket(socket)
    .map(makeUrl)
    .map(new Rendering(_))
    .flatMap(_.loaded)
    .map(_.render())
    .buffer(1.second).map(_.size).foreach(println)

  def makeUrl(messageEvent: MessageEvent) = {
    val arrayBuffer = messageEvent.data.asInstanceOf[ArrayBuffer]
    val properties = js.Dynamic.literal("type" -> "image/jpeg").asInstanceOf[BlobPropertyBag]
    val blob = new Blob(js.Array(arrayBuffer), properties)
    CanvasControls.URL.createObjectURL(blob)
  }
}
