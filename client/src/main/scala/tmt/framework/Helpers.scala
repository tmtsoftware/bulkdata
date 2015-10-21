package tmt.framework

import org.scalajs.dom.html._
import rx._

import scala.scalajs.js
import scalatags.JsDom.all._

object Helpers {
  def setValue(selection: Var[String]): js.ThisFunction = { e: Select =>
    selection() = e.value
  }

  def setSource(websocketRx: WebsocketRx, labelToUpdate: Label) = {
    websocketRx.setUrl()
    labelToUpdate.textContent = websocketRx.wsServer()
  }

  def makeOptions(dropdownText: String, values: Seq[String], onchange: String => Unit, selectedValue: String, cssWidth: String = "100px") = {
    val downArrow = " \u25BE"
    def validSelection = selectedValue != "" && values.contains(selectedValue)
    val _dropdownText  = if(validSelection) selectedValue else dropdownText
    val button1 = button(
      `type` := "button",
      `class` := "btn btn-default dropdown-toggle btn-block",
      "data-toggle".attr := "dropdown")(_dropdownText + " ")(
        span(`class` := "caret")
      ).render

    div(`class` := "dropdown",
      button1,
      ul(`class` := "dropdown-menu")(width := "100%"){
        values.map { label =>
          li(a(href := "#", onclick := {() => onchange(label); button1.textContent = label + downArrow})(label))
        }
      }
    )(display := "inline-block", width := cssWidth)
  }
}
