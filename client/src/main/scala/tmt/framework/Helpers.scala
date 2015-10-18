package tmt.framework

import org.scalajs.dom.html._
import rx._

import scala.scalajs.js
import scalatags.JsDom.all._

object Helpers {
  val optionHint = option(selected := true, disabled, hidden := true, value := "")

  def makeOptions(values: Seq[String], selectedValue: String) =
    makeOptions2(values, values, selectedValue)

  def makeOptions2(
    values: Seq[String],
    labels: Seq[String],
    selectedValue: String
  ) = values.zip(labels).map {
    case (`selectedValue`, l) => option(value := selectedValue, selected := "selected")(l)
    case (v, l)               => option(value := v)(l)
  }

  def setValue(selection: Var[String]): js.ThisFunction = { e: Select =>
    selection() = e.value
  }
}

