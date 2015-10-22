package tmt.views

import tmt.app.DataStore
import tmt.css.Styles
import scala.concurrent.ExecutionContext
import scalatags.JsDom.all._

class ConfigurationControlsView(dataStore: DataStore)(implicit ec: ExecutionContext) extends View {
  def frag = {
    div(`class` := "col-lg-2",
      new ThrottleView(dataStore.data).frag,
      new SubscriptionView(dataStore.data).frag
    )(Styles.controlsView)
  }
}
