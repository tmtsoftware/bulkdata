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
  val controlsView = Seq(
    leftFloat,
    width := "16%"
  )
  val imageStreamView = Seq(
    leftFloat,
    margin := "13%",
    marginTop:= "0px"
  )
  val frequencyStreamView = Seq(
    rightFloat,
    width := "16%"
  )
}
