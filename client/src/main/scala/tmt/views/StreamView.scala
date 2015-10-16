package tmt.views

import tmt.framework.Framework._
import tmt.framework.Helpers._
import tmt.images.ImageRendering
import tmt.metrics.MetricsRendering
import tmt.shared.models.{HostMappings, ItemType, RoleIndex}

import scalatags.JsDom.all._

class StreamView(roleIndex: RoleIndex, hostMappings: HostMappings) {
  val sourceServers = roleIndex.outputTypeIndex.getServers(ItemType.Image)
  val frequencyServers = roleIndex.outputTypeIndex.getServers(ItemType.Frequency)
  import tmt.common.Constants._

  def frag = {
    div(
      div(
        "Frequency Computing Node",
        select(onchange := setValue(MetricsRendering.frequencyNode))(
          optionHint("select"),
          makeOptions2(frequencyServers.map(hostMappings.getHost), frequencyServers, "")
        ),
        br,
        span(id := "per-sec")(MetricsRendering.frequency)
      ), br,
      div(
        streamSelectionView("source-selection1", "canvas1"),
        streamSelectionView("source-selection2", "canvas2")
      )
    )
  }


  private def streamSelectionView(selectionId: String, canvasId: String) = {
    val ImageRendering = new ImageRendering
    val cvs = canvas(id := canvasId, widthA := canvasWidth, heightA := canvasHeight).render
    ImageRendering.drawOn(cvs)
    div(
      "Image Source",
      select(id := selectionId, onchange := setValue(ImageRendering.imageNode))(
        optionHint("select"),
        makeOptions2(sourceServers.map(hostMappings.getHost), sourceServers, "")
      ),
      cvs
    )(float := "left", width := s"${canvasWidth + 50}px", height := s"${canvasHeight + 50}px")
  }
}
