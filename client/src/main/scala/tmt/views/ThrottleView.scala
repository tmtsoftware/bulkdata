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
    label("Throttle"),
    hr(Styles.hr),
    Rx {
      makeOptions("select wavefront", dataStore.wavefrontServers(), server)
    },
    input(`class` := "form-control")(
      `type` := "text", placeholder := "new rate", onchange := setValue(rate)
    )(Styles.topMargin),
    button(`class` := "btn btn-block btn-default active")(
      onclick := {() => throttle(server(), rate())}
    )("Change")(Styles.topMargin),
    br, br
  )

  def throttle(server: String, rate: String) = Ajax.post(s"$server/throttle/$rate")

}
