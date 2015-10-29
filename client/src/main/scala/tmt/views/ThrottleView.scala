package tmt.views

import org.scalajs.dom.ext.Ajax
import rx._
import tmt.app.ViewData
import tmt.framework.Framework._
import tmt.framework.Helpers._

import scalatags.JsDom.all._

class ThrottleView(dataStore: ViewData) extends View {
  val server = Var("")
  val rate   = Var("")

  val noData = Rx(server().isEmpty || dataStore.wavefrontServers().isEmpty)

  val disabledStyle = Rx(if(noData()) "disabled" else "")

  def viewTitle = h5("Throttle")

  def viewContent = div(
    label("Wavefront to throttle"),
    makeSelection(dataStore.wavefrontServers, server),

    label("New rate"),
    p(cls := "range-field")(
      input(`type` := "range", min := 3, max := 100)(
        onchange := { setValue(rate) }
      )
    )
  )

  def viewAction = {
    button(cls := Rx(s"waves-effect waves-light btn ${disabledStyle()}"))(
      `type` := "submit",
      onclick := { () => if(!noData()) throttle(server(), rate()) }
    )("Change")
  }

  def throttle(server: String, rate: String) = Ajax.post(s"$server/throttle/$rate")

}
