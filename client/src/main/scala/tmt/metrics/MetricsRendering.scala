package tmt.metrics

import boopickle.Default
import monifu.reactive.Observable
import org.scalajs.dom._
import rx._
import rx.ops._
import tmt.common.Stream
import tmt.shared.models.PerSecMetric

import scala.scalajs.js.typedarray.{ArrayBuffer, TypedArrayBuffer}
import monifu.concurrent.Implicits.globalScheduler

object MetricsRendering {
  val frequencyNode = Var("")
  val frequency = Var("")

  val metricSocket = Var(Option.empty[WebSocket])

  val stream = metricSocket.map {
    case None         => Observable.empty
    case Some(socket) => Stream.socket(socket).map(MetricsRendering.makeItem)
  }

  Obs(stream) {
    import prickle._
    stream().foreach(metric => frequency() = s"frequency: " + Pickle.intoString(metric))
  }

  Obs(frequencyNode, skipInitial = true) {
    metricSocket().foreach(_.close())
    val node = frequencyNode()
    val newSocket = new WebSocket(node)
    newSocket.binaryType = "arraybuffer"
    metricSocket() = Some(newSocket)
  }

  def makeItem(messageEvent: MessageEvent) = {
    val arrayBuffer = messageEvent.data.asInstanceOf[ArrayBuffer]
    val byteBuffer = TypedArrayBuffer.wrap(arrayBuffer)
    Default.Unpickle[PerSecMetric].fromBytes(byteBuffer)
  }
}
