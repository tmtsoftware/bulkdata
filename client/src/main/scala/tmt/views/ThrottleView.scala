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
    label("Change input rate"),
    hr(Styles.hr),
    Rx { makeOptions1("select source", dataStore.sourceServers(), server() = _, server())},
    input(`type` := "text", placeholder := "new rate", `class` := "form-control" , onchange := setValue(rate))(Styles.topMargin),
    button(onclick := {() => throttle(server(), rate())}, `class` := "btn btn-block btn-default active")("Change")(Styles.topMargin),
    br, br
  )

  def throttle(server: String, rate: String) = Ajax.post(s"$server/throttle/$rate")

}

object Styles {
  val hr = {
    Seq(
      marginTop := "5px",
      marginBottom := "10px"
    )
  }

  val normalFontWeight = {
    fontWeight := "400"
  }

  val topMargin = marginTop := "10px"

  val ul = {
    Seq(
      listStyle := "none",
      padding := "0",
      margin := "0"
    )
  }
}
