package tmt.framework

import org.scalajs.dom.html._
import rx._

import scala.scalajs.js
import scalatags.JsDom.all._

object Helpers {
  val optionHint = option(selected := true, disabled, hidden := true, value := "")

  def makeOptions(values: Seq[String], selectedValue: String) = values.map {
    case v@`selectedValue` => option(value := v, selected := true)(v)
    case v                 => option(value := v)(v)
  }

  def setValue(selection: Var[String]): js.ThisFunction = { e: Select =>
    selection() = e.value
  }
}
