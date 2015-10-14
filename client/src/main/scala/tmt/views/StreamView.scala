package tmt.views

import tmt.framework.Framework._
import tmt.framework.Helpers._
import tmt.metrics.MetricsRendering
import tmt.shared.models.{ItemType, Role, HostMappings, RoleIndex}

import scalatags.JsDom.all._

class StreamView(roleIndex: RoleIndex, hostMappings: HostMappings) {
  val sourceServers = roleIndex.outputTypeIndex.getServers(ItemType.Image)
  val frequencyServers = roleIndex.outputTypeIndex.getServers(ItemType.Frequency)

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
