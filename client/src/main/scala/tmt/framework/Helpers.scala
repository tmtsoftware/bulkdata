package tmt.framework

import org.scalajs.dom.html._
import rx._
import rx.core.Rx
import tmt.framework.Framework._

import scala.scalajs.js
import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._
object Helpers {

  val optionHint = option(selected := true, disabled, hidden := true, value := "")

  val formGroup = div(cls := "form-group")

  def formControl(element: TypedTag[Element]) = element(cls := "form-control")

  def makeSelection(options: Rx[Seq[String]], selection: Var[String]) = Rx {
    formControl(select)(onchange := setValue(selection))(
      optionHint("select"),
      makeOptions(options(), selection())
    )
  }

  def makeForm(desc: String, options: Rx[Seq[String]], websocketRx: WebsocketRx) = {
    formGroup(cls := "form-inline")(
      label(desc),
      makeSelection(options, websocketRx.wsServer),
      formControl(button)(onclick := { () => websocketRx.setUrl() })("Set"),
      Rx(label(s"Current value: ${websocketRx.selectedServer()}"))
    )
  }
  def setValue(selection: Var[String]): js.ThisFunction = { e: Select =>
    selection() = e.value
  }

  private def makeOptions(values: Seq[String], selectedValue: String) = values.map {
    case v@`selectedValue` => option(value := v, selected := true)(v)
    case v                 => option(value := v)(v)
  }
}
