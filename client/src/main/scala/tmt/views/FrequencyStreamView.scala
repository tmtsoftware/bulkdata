package tmt.views

import monifu.concurrent.Scheduler
import rx._
import tmt.app.ViewData
import tmt.css.Styles
import tmt.framework.Helpers._
import tmt.metrics.MetricsRendering
import tmt.framework.Framework._
import scalatags.JsDom.all._

class FrequencyStreamView(viewData: ViewData)(implicit scheduler: Scheduler) extends View {

  val metricsRendering = new MetricsRendering(viewData)
  val selectedServer = label().render

  def frag = div(`class` := "col-lg-2")(
    label("Frequency Computing Node"),
    hr(Styles.hr),
    Rx {
      makeOptions("select", viewData.frequencyServers(), metricsRendering.wsServer)
    },
    button(`class` := "btn btn-block btn-default active")(
      onclick := {() => setSource(metricsRendering, selectedServer)}
    )("Set")(Styles.topMargin),
    br,
    selectedServer,
    br,
    span(metricsRendering.frequency)
  )(Styles.frequencyStreamView)
}
