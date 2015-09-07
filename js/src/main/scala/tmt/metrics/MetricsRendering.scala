package tmt.metrics

import boopickle.{Default, Pickler}
import monifu.concurrent.Implicits.globalScheduler
import org.scalajs.dom._
import org.scalajs.dom.html.Span
import tmt.common.Stream

import scala.scalajs.js.typedarray.{ArrayBuffer, TypedArrayBuffer}

object MetricsRendering {
  def render[T: Pickler](socket: WebSocket, span: Span) = Stream.socket(socket)
    .map(makeItem[T])
    .map(x => span.textContent = x.toString)
    .foreach(println)

  def makeItem[T: Pickler](messageEvent: MessageEvent) = {
    val arrayBuffer = messageEvent.data.asInstanceOf[ArrayBuffer]
    val byteBuffer = TypedArrayBuffer.wrap(arrayBuffer)
    Default.Unpickle[T].fromBytes(byteBuffer)
  }
}
