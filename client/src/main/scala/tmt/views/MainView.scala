package tmt.views

import scalatags.JsDom.all._

class MainView(leftColumn: LeftColumn, streamView: StreamView) extends View {

  def frag = {
    div(cls := "container-fluid")(
      div(cls := "row")(
        leftColumn.frag,
        streamView.frag
      )
    )
  }
}
