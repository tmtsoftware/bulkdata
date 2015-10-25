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
    label("Wavefront to throttle"),
    makeSelection(dataStore.wavefrontServers, server),

    label("New rate"),
    p(cls := "range-field")(
      input(`type` := "range", min := 3, max := 100, onchange := { setValue(rate) })
    ),

    button(cls := "waves-effect waves-light btn")(
    `type` := "submit",
      onclick := {() => throttle(server(), rate())}
    )("Change")
  )


  def throttle(server: String, rate: String) = Ajax.post(s"$server/throttle/$rate")

}
