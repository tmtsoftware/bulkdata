package tmt.views

import monifu.concurrent.Implicits.globalScheduler
import monifu.reactive.Observable
import org.scalajs.dom.WebSocket
import rx._
import rx.ops._
import tmt.common.Stream
import tmt.framework.Framework._
import tmt.framework.Helpers._
import tmt.metrics.MetricsRendering
import tmt.shared.models.{HostMappings, PerSecMetric, RoleMappings}

import scalatags.JsDom.all._

class StreamView(roleMappings: RoleMappings, hostMappings: HostMappings) {
  val frequencyServers = roleMappings.getServers("frequency")
  val sourceServers = roleMappings.getServers("source")

  val frequencyNode = Var("")
  val frequency = Var("")

  val metricSocket = Var(Option.empty[WebSocket])

  val stream = metricSocket.map {
    case None         => Observable.empty
    case Some(socket) => Stream.socket(socket).map(MetricsRendering.makeItem[PerSecMetric])
  }

  Obs(stream) {
    import upickle.default._
    stream().foreach(metric => frequency() = s"frequency: " + write(metric))
  }

  Obs(frequencyNode, skipInitial = true) {
    metricSocket().foreach(_.close())
    val node = frequencyNode()
    val newSocket = new WebSocket(node)
    newSocket.binaryType = "arraybuffer"
    metricSocket() = Some(newSocket)
  }

  def frag = {
    div(
      div(
        label("Frequency Computing Node"),
        select(onchange := setValue(frequencyNode))(
          optionHint,
          makeOptions2(frequencyServers.map(hostMappings.getHost), frequencyServers)
        ),
        br,
        span(id := "per-sec")(frequency)
      ),
      div(
        label("Image Source"),
        select(id := "source-selection")(
          optionHint,
          makeOptions2(sourceServers.map(hostMappings.getHost), sourceServers)
        ),
        br,
        canvas(id := "canvas")
      )
    )
  }
}
