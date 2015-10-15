package tmt.views

import tmt.framework.Framework._
import tmt.framework.Helpers._
import tmt.metrics.MetricsRendering
import tmt.shared.models.{HostMappings, ItemType, RoleIndex}

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
        streamSelectionView("source-selection1", "canvas1"),
        streamSelectionView("source-selection2", "canvas2")
      )
    )
  }

  private def streamSelectionView(selectionId: String, canvasId: String) = {
    div(
      label("Image Source"),
      select(id := selectionId)(
        optionHint("select-node"),
        makeOptions2(sourceServers.map(hostMappings.getHost), sourceServers)
      ),
      canvas(id := canvasId, width := "500px", height := "500px")
    )(float := "left", width := "550px", height := "550px")
  }
}
