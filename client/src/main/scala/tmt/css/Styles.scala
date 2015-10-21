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
  val ul = Seq(
    listStyle := "none",
    padding := "0",
    margin := "0"
  )
  val connectionNodeLabel = Seq(
    width := "80px",
    Styles.normalFontWeight
  )
  val arrowLabel = Seq(
    width := "30px",
    Styles.normalFontWeight
  )
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
