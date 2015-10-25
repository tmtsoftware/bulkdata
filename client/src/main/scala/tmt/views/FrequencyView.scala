package tmt.views

import monifu.concurrent.Scheduler
import rx.core.Rx
import tmt.app.ViewData
import tmt.framework.Framework._
import tmt.framework.Helpers._
import tmt.metrics.MetricsRendering

import scalatags.JsDom.all._

class FrequencyView(viewData: ViewData)(implicit scheduler: Scheduler) extends View {
  val metricsRendering = new MetricsRendering(viewData)
  metricsRendering.selectedServer() = "Frequency"

  def viewTitle = Rx(h5(metricsRendering.selectedServer().capitalize))

  def viewContent = div(metricsRendering.frequency)

  def viewAction = makeForm(viewData.frequencyServers, metricsRendering)
}
