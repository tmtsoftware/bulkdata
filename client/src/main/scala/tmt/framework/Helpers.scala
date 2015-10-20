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

  def setSource(websocketRx: WebsocketRx, labelToUpdate: Label) = {
    websocketRx.setUrl()
    labelToUpdate.textContent = websocketRx.wsServer()
  }

  def makeOptions1(dropdownText: String, values: Seq[String], onchange: String => Unit, selectedValue: String) = {
    def validSelection = selectedValue != "" && values.contains(selectedValue)
    val _dropdownText  = if(validSelection) selectedValue else dropdownText
    val button1 = button(
      `type` := "button",
      `class` := "btn btn-default dropdown-toggle",
      "data-toggle".attr := "dropdown")(_dropdownText + " ")(
        span(`class` := "caret")
      ).render

    div(`class` := "dropdown",
      button1,
      ul(`class` := "dropdown-menu"){
        values.map { label =>
          li(a(href := "#", onclick := {() => onchange(label); button1.textContent = label + " \u25BE"})(label))
        }
      }
    )
  }
}
