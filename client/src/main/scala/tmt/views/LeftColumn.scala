package tmt.views

import scalatags.JsDom.all._

class LeftColumn(
  throttleView: ThrottleView,
  frequencyView: FrequencyView,
  subscriptionView: SubscriptionView) extends View {

  def frag = div(cls := "col-lg-2")(
    throttleView.frag,
    frequencyView.frag,
    subscriptionView.frag
  )
}
