package tmt.views

import org.scalajs.dom.ext.Ajax
import rx._
import tmt.app.{ViewData, DataStore}
import tmt.framework.Framework._
import tmt.framework.Helpers._

import scalatags.JsDom.all._

class ThrottleView(dataStore: ViewData) {
  val server = Var("")
  val rate = Var("")

  def frag = div(
    "Throttle",
    Rx {
      select(id := "server-name", onchange := setValue(server))(
        optionHint(s"select source"),
        makeOptions(dataStore.sourceServers(), server())
      )
    },
    input(`type` := "text", id := "new-rate", placeholder := "new rate", onchange := setValue(rate)),
    button(id := "change-rate", `type` := "button", onclick := {() => Ajax.post(s"${server()}/throttle/${rate()}")})("Change"),
    br, br
  )
}
