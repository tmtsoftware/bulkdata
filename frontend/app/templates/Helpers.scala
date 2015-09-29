package templates

import scalatags.Text.all._

object Helpers {
  val optionHint = option(selected := true, disabled, hidden := true, value := "")
  def makeOptions(values: Seq[String]) = makeOptions2(values, values)
  def makeOptions2(values: Seq[String], labels: Seq[String]) = values.zip(labels).map { case (v, l) =>
    option(value := v)(l)
  }
}
