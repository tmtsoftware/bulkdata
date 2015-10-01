package templates

import models.HostMappings
import templates.Helpers._
import tmt.shared.models.RoleMappings

import scalatags.Text.all._

class StreamView(roleMappings: RoleMappings, hostMappings: HostMappings) extends View {
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
