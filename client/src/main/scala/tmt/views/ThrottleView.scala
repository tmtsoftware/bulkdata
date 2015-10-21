package tmt.views

import org.scalajs.dom.ext.Ajax
import rx._
import tmt.app.ViewData
import tmt.css.Styles
import tmt.framework.Framework._
import tmt.framework.Helpers._

import scalatags.JsDom.all._

class ThrottleView(dataStore: ViewData) extends View {
  val server = Var("")
  val rate = Var("")

  def frag = div(
    label("Change input rate"),
    hr(Styles.hr),
    Rx { makeOptions("select source", dataStore.sourceServers(), server() = _, server(), "100%")},
    input(`type` := "text", placeholder := "new rate", `class` := "form-control" , onchange := setValue(rate))(Styles.topMargin),
    button(onclick := {() => throttle(server(), rate())}, `class` := "btn btn-block btn-default active")("Change")(Styles.topMargin),
    br, br
  )

  def throttle(server: String, rate: String) = Ajax.post(s"$server/throttle/$rate")

}
