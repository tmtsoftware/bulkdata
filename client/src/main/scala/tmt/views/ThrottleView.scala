package tmt.views

import org.scalajs.dom.ext.Ajax
import rx._
import tmt.shared.models.{Role, RoleIndex}

import scalatags.JsDom.all._
import tmt.framework.Helpers._

class ThrottleView(roleIndex: RoleIndex) {

  val server = Var("")
  val rate = Var("")

  def frag = div(
    label("Change input rate"), br, br,
    label("Source"),
    select(id := "server-name", onchange := setValue(server))(
      optionHint("select-node"),
      makeOptions(roleIndex.getServers(Role.Source))
    ),
    input(`type` := "text", id := "new-rate", placeholder := "new rate", onchange := setValue(rate)),
    button(id := "change-rate", `type` := "button", onclick := {() => Ajax.post(s"${server()}/throttle/${rate()}")})("Change"),
    br, br
  )
}
