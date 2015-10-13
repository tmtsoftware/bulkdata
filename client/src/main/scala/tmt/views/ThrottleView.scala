package tmt.views

import org.scalajs.dom.ext.Ajax
import rx._
import tmt.shared.models.{Role, RoleMappingSet}

import scalatags.JsDom.all._
import tmt.framework.Helpers._

class ThrottleView(roleMappings: RoleMappingSet) {

  val server = Var("")
  val rate = Var("")

  def frag = div(
    label("Change input rate"), br, br,
    label("Source"),
    select(id := "server-name", onchange := setValue(server))(
      optionHint("select-node"),
      makeOptions(roleMappings.getServersByRole(Role.Source))
    ),
    input(`type` := "text", id := "new-rate", placeholder := "new rate", onchange := setValue(rate)),
    button(id := "change-rate", `type` := "button", onclick := {() => Ajax.post(s"${server()}/throttle/${rate()}")})("Change"),
    br, br
  )
}
