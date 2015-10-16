package tmt.views

import org.scalajs.dom.ext.Ajax
import rx._
import tmt.shared.models.{Role, RoleIndex}

import scalatags.JsDom.all._
import tmt.framework.Helpers._
import tmt.framework.Framework._

class ThrottleView(roleIndex: Var[RoleIndex]) {

  val server = Var("")
  val rate = Var("")
  val servers = Rx(roleIndex().getServers(Role.Source))

  def frag = div(
    "Throttle",
    Rx {
      select(id := "server-name", onchange := setValue(server))(
        optionHint(s"select source"),
        makeOptions(servers(), server())
      )
    },
    input(`type` := "text", id := "new-rate", placeholder := "new rate", onchange := setValue(rate)),
    button(id := "change-rate", `type` := "button", onclick := {() => Ajax.post(s"${server()}/throttle/${rate()}")})("Change"),
    br, br
  )
}
