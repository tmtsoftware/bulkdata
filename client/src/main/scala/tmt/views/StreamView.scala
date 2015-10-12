package tmt.views

import tmt.shared.models.{HostMappings, RoleMappings}

import scalatags.JsDom.all._
import tmt.framework.Helpers._

class StreamView(roleMappings: RoleMappings, hostMappings: HostMappings) {
  val frequencyServers = roleMappings.getServers("frequency")
  val sourceServers = roleMappings.getServers("source")
  
  def frag = {
    div(
      div(
        label("Frequency Computing Node"),
        select(id := "frequency-selection")(
          optionHint,
          makeOptions2(frequencyServers.map(hostMappings.getHost), frequencyServers)
        ),
        br,
        span(id := "per-sec")
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
