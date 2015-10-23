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
        div(cls := "col-md-2")(
          throttleView.frag,
          frequencyView.frag,
          subscriptionView.frag
        ),
        div(cls := "col-md-5")(
          streamView.frag,
          streamView.frag
        )
      )
    )
  }
}
