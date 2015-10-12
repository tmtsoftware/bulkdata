package tmt.views

import tmt.framework.Framework._
import tmt.framework.Helpers._
import tmt.metrics.MetricsRendering
import tmt.shared.models.{HostMappings, RoleMappings}

import scalatags.JsDom.all._

class StreamView(roleMappings: RoleMappings, hostMappings: HostMappings) {
  val frequencyServers = roleMappings.getServers("frequency")
  val sourceServers = roleMappings.getServers("source")

  def frag = {
    div(
      div(
        label("Frequency Computing Node"),
        select(onchange := setValue(MetricsRendering.frequencyNode))(
          optionHint("select-node"),
          makeOptions2(frequencyServers.map(hostMappings.getHost), frequencyServers)
        ),
        br,
        span(id := "per-sec")(MetricsRendering.frequency)
      ),
      div(
        label("Image Source"),
        select(id := "source-selection")(
          optionHint("select-node"),
          makeOptions2(sourceServers.map(hostMappings.getHost), sourceServers)
        ),
        br,
        canvas(id := "canvas")
      )
    )
  }
}
