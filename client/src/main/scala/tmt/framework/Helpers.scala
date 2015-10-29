package tmt.framework

import org.scalajs.dom.html._
import rx._
import rx.core.Rx
import tmt.framework.Framework._

import scala.scalajs.js
import scalatags.JsDom.all._
object Helpers {

  val optionHint = option(selected := true, disabled, value := "")

  def makeSelection(options: Rx[Seq[String]], selection: Var[String]) = Rx {
    select(cls := "browser-default")(onchange := setValue(selection))(
      optionHint("select"),
      makeOptions(options(), selection())
    )
  }

  def makeForm(options: Rx[Seq[String]], formRx: FormRx) = {
    val noData = Rx(formRx.server().isEmpty || options().isEmpty)
    val disabledStyle = Rx(if(noData()) "disabled" else "")

    div(cls := "row")(
      div(cls := "col l8")(makeSelection(options, formRx.server)),

      button(cls := Rx(s"waves-effect waves-light btn col l4 ${disabledStyle()}"))(
        onclick := { () => if(!noData()) formRx.action() }
      )("Set")
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
