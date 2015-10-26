package tmt.views

import scalatags.JsDom.all._

class Body(
  throttleView: ThrottleView,
  subscriptionView: SubscriptionView,
  frequencyViews: Seq[FrequencyView],
  streamViews: Seq[ImageView],
  scienceImageViews: Seq[ScienceImageView]
) {

  def layout = div(cls := "container")(
    div(cls := "row")(
      div(cls := "col l4")(
        makeCard(throttleView),
        makeCard(subscriptionView),
        scienceImageViews.map(makeCard)
      ),
      div(cls := "col l8")(
        (frequencyViews ++ streamViews).map(makeCard)
      )
    )
  )

  def makeCard(view: View) = div(cls := "card")(
    div(cls := "card-content")(
      view.viewTitle,
      view.viewContent
    ),
    div(cls := "card-action")(
      view.viewAction
    )
  )
}
