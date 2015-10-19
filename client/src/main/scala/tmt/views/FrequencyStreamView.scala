package tmt.views

import monifu.concurrent.Scheduler
import rx._
import tmt.app.ViewData
import tmt.framework.Helpers._
import tmt.metrics.MetricsRendering
import tmt.framework.Framework._
import scalatags.JsDom.all._

class FrequencyStreamView(viewData: ViewData)(implicit scheduler: Scheduler) extends View {

  val metricsRendering = new MetricsRendering(viewData)
  val selectedServer = label().render

  def frag = div(`class` := "col-lg-3",
    "Frequency Computing Node",
    Rx {
      select(onchange := setValue(metricsRendering.wsServer))(
        optionHint("select"),
        makeOptions(viewData.frequencyServers(), metricsRendering.wsServer())
      )
    },
    button(onclick := {() => setSource(metricsRendering, selectedServer)})("Set"),
    br,
    selectedServer,
    br,
    span(id := "per-sec")(metricsRendering.frequency)
  )
}
