package tmt.views

import org.scalajs.dom.ext.Ajax
import rx._
import tmt.app.ViewData
import tmt.framework.Framework._
import tmt.framework.Helpers._

import scalatags.JsDom.all._

class ThrottleView(dataStore: ViewData) extends View {
  val server = Var("")
  val rate = Var("")

  def frag = div(
    "Throttle",
    Rx {
      select(id := "server-name", onchange := setValue(server))(
        optionHint(s"select wavefront"),
        makeOptions(dataStore.wavefrontServers(), server())
      )
    },
    input(`type` := "text", id := "new-rate", placeholder := "new rate", onchange := setValue(rate)),
    button(id := "change-rate", `type` := "button", onclick := {() => throttle(server(), rate())})(
      "Change"
    ),
    br, br
  )

  def throttle(server: String, rate: String) = Ajax.post(s"$server/throttle/$rate")

}
