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
    input(`type` := "text", placeholder := "new rate", `class` := "input-sm form-control" , onchange := setValue(rate))(Styles.input),
    button(`type` := "button", onclick := {() => throttle(server(), rate())})(
      "Change"
    )(Styles.button),
    br, br
  )(Styles.leftDiv)

  def throttle(server: String, rate: String) = Ajax.post(s"$server/throttle/$rate")

}

object Styles {
  val leftDiv = {
    Seq(
      margin := "10px",
      padding := "5px"
    )
  }


  val hr = {
    Seq(
      marginTop := "5px",
      marginBottom := "10px"
    )
  }

  val normalFontWeight = {
    fontWeight := "400"
  }

  val input = marginTop := "10px"

  val button = {
    Seq(
      width := "100%",
      height := "30px",
      marginTop := "10px",
      borderRadius := "5px"
    )
  }
}
