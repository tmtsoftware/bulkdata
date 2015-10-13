package tmt.views

import org.scalajs.dom.ext.Ajax
import rx._
import tmt.shared.models.{Role, RoleMappings}

import scalatags.JsDom.all._
import tmt.framework.Helpers._

class ThrottleView(roleMappings: RoleMappings) {

  val server = Var("")
  val rate = Var("")

  def frag = div(
    label("Change input rate"), br, br,
    label("Source"),
    select(id := "server-name", onchange := setValue(server))(
      optionHint("select-node"),
      makeOptions(roleMappings.getServers(Role.Source))
    ),
    input(`type` := "text", id := "new-rate", placeholder := "new rate", onchange := setValue(rate)),
    button(id := "change-rate", `type` := "button", onclick := {() => Ajax.post(s"${server()}/throttle/${rate()}")})("Change"),
    br, br
  )
}
