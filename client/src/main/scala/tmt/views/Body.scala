package tmt.views

import scalatags.JsDom.all._

class Body(
  throttleView: ThrottleView,
  frequencyView: FrequencyView,
  subscriptionView: SubscriptionView,
  streamView: ImageView
) {

  def layout = {
    div(cls := "container")(
      div(cls := "row")(
        div(cls := "col l4")(
          div(cls := "section")(
            h5("Throttle"),
            throttleView.frag
          ),
          div(cls := "divider"),
          div(cls := "section")(
            h5("Connect"),
            subscriptionView.frag
          )
        ),
        div(cls := "col l8")(
          div(cls := "section")(
            h5("Frequency"),
            frequencyView.frag
          ),
          div(cls := "divider"),
          div(cls := "section")(
            h5("Wavefront"),
            streamView.frag
          ),
          div(cls := "divider"),
          div(cls := "section")(
            h5("Wavefront"),
            streamView.frag
          )
        )
      )
    )
  }
}
