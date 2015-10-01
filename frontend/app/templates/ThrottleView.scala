package templates

import tmt.shared.models.RoleMappings

import scalatags.Text.all._
import Helpers._

class ThrottleView(roleMappings: RoleMappings) extends View {
  def frag = div(
    label("Change input rate"), br, br,
    label("Source"),
    select(id := "server-name")(
      makeOptions(roleMappings.getServers("source"))
    ),
    input(`type` := "text", id := "new-rate", placeholder := "new rate"), br, br,
    button(id := "change-rate", `type` := "button")("Change")
  )
}
