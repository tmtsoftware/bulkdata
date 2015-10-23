package tmt.framework

import org.scalajs.dom.html._
import rx._

import scala.scalajs.js
import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._

object Helpers {

  val formGroup = div(cls := "form-group")

  def formControl(element: TypedTag[Element]) = element(cls := "form-control")

  def makeSelection(options: Rx[Seq[String]], selection: Var[String]) = Rx {
    formControl(select)(onchange := setValue(selection))(
      makeOptions(options(), selection())
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
