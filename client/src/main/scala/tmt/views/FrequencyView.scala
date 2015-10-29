package tmt.views

import monifu.concurrent.Scheduler
import rx.core.Rx
import tmt.app.ViewData
import tmt.framework.Framework._
import tmt.framework.Helpers._
import tmt.metrics.FrequencyRendering

import scalatags.JsDom.all._

class FrequencyView(viewData: ViewData)(implicit scheduler: Scheduler) extends View {
  val frequencyRendering = new FrequencyRendering(viewData)

  def viewTitle = frequencyRendering.title

  def viewContent = div(frequencyRendering.frequency)

  def viewAction = makeForm(viewData.frequencyServers, frequencyRendering)
}
