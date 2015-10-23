package tmt.views

import monifu.concurrent.Scheduler
import rx._
import tmt.app.ViewData
import tmt.framework.Framework._
import tmt.framework.Helpers._
import tmt.metrics.MetricsRendering

import scalatags.JsDom.all._

class FrequencyView(viewData: ViewData)(implicit scheduler: Scheduler) extends View {
  val metricsRendering = new MetricsRendering(viewData)

  def frag = {
    div(
      "Frequency Computing Node",
      Rx {
        select(onchange := setValue(metricsRendering.wsServer))(
          optionHint("select"),
          makeOptions(viewData.frequencyServers(), metricsRendering.wsServer())
        )
      },
      button(onclick := { () => metricsRendering.setUrl() })("Set"),
      br,
      span(metricsRendering.frequency)
    )
  }
}
