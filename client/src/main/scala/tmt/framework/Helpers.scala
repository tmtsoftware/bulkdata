package tmt.framework

import org.scalajs.dom.html._
import rx._
import tmt.css.Styles

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

  def makeOptions(dropdownText: String, values: Seq[String], selection: Var[String], cssWidth: String = "100%") = {
    val selectedValue = selection()
    val isSelectionValid = selectedValue != "" && values.contains(selectedValue)
    val displayText  = if (isSelectionValid) selectedValue else dropdownText
    val button1 = createButton(displayText, _ => (), "btn btn-default", Styles.selectionButton).render

    div(`class` := "btn-group")(
      button1,
      button(`class` := "btn btn-default dropdown-toggle")(
        "data-toggle".attr := "dropdown",
        span(`class` := "caret")
      )(Styles.selectionCaret),
      ul(`class` := "dropdown-menu")(Styles.blockDisplay) {
        values.map { label =>
          li(
            a(href := "#", onclick := {() => selection() = label; button1.textContent = label})(label))
        }
      }
    )(display := "inline-block", width := cssWidth)
  }

  private def createButton(text: String, onclickAction: Unit => Unit, cssClasses: String, modifiers: Modifier*) =
    button(`class` := cssClasses)(onclick := {() => onclickAction()})(text)(modifiers)

  def defaultButton(text: String, onclickAction: Unit => Unit) =
    createButton(text, onclickAction, "btn btn-default active")

  def blockButton(text:String, onclickAction: Unit => Unit) =
    createButton(text, onclickAction, "btn btn-block btn-default active")(Styles.topMargin)
}
