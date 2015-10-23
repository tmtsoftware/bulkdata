package tmt.views

import scalatags.JsDom.all._

class Body(
  throttleView: ThrottleView,
  frequencyView: FrequencyView,
  subscriptionView: SubscriptionView,
  streamView: ImageView
) {

  def layout = {
    div(cls := "container-fluid")(
      div(cls := "row")(
        div(cls := "col-md-3")(
          throttleView.frag,
          subscriptionView.frag
        ),
        div(cls := "col-md-5")(
          frequencyView.frag,
          streamView.frag,
          streamView.frag
        )
      )
    )
  }
}
