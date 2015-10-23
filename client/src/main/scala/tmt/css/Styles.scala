package tmt.css

import scalatags.JsDom.all._

object Styles {

  val leftFloat = float := "left"
  val rightFloat = float := "right"
  val hr = Seq(
    marginTop := "5px",
    marginBottom := "10px"
  )
  val normalFontWeight = fontWeight := "400"
  val topMargin = marginTop := "10px"
  val centerAlign = textAlign := "center"
  val canvas = Seq(
    backgroundColor := "#f1f1f1",
    topMargin
  )
  val blockDisplay = width := "100%"
}
