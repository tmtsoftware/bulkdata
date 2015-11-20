package tmt.metrics

import boopickle.Default
import monifu.concurrent
import monifu.reactive.Observable
import org.scalajs.dom._
import rx._
import rx.ops._
import tmt.app.ViewData
import tmt.common.Stream
import tmt.framework.WebsocketRx
import tmt.shared.models.PerSecMetric

import scala.scalajs.js.typedarray.{ArrayBuffer, TypedArrayBuffer}

class FrequencyRendering(viewData: ViewData)(implicit scheduler: concurrent.Scheduler) extends WebsocketRx("Metric", viewData) {

  def cleanup() = {
    frequency() = ""
  }

  val frequency = Var("")

  val stream = webSocket.map {
    case None         => Observable.empty
    case Some(socket) => Stream.socket(socket).map(makeItem)
  }

  Obs(stream) {
    import prickle._
    stream().foreach(metric => frequency() = Pickle.intoString(metric))
  }

  def makeItem(messageEvent: MessageEvent) = {
    val arrayBuffer = messageEvent.data.asInstanceOf[ArrayBuffer]
    val byteBuffer = TypedArrayBuffer.wrap(arrayBuffer)
    Default.Unpickle[PerSecMetric].fromBytes(byteBuffer)
  }
}
