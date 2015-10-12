package tmt.metrics

import boopickle.{Default, Pickler}
import org.scalajs.dom._

import scala.scalajs.js.typedarray.{ArrayBuffer, TypedArrayBuffer}

object MetricsRendering {
  def makeItem[T: Pickler](messageEvent: MessageEvent) = {
    val arrayBuffer = messageEvent.data.asInstanceOf[ArrayBuffer]
    val byteBuffer = TypedArrayBuffer.wrap(arrayBuffer)
    Default.Unpickle[T].fromBytes(byteBuffer)
  }
}
