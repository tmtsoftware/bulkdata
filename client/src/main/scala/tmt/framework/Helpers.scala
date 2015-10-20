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

  def makeOptions(dropdownText: String, values: Seq[String], onchange: String => Unit, selectedValue: String) = {
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
    )(float := "left")
  }
}
