package tmt.views

import monifu.concurrent.Scheduler
import rx._
import tmt.app.ViewData
import tmt.framework.Framework._
import tmt.framework.Helpers._
import tmt.images.ImageRendering
import tmt.metrics.MetricsRendering

import scalatags.JsDom.all._

class StreamView(viewData: ViewData)(implicit scheduler: Scheduler) extends View {

  import tmt.common.Constants._
  val metricsRendering = new MetricsRendering(viewData)

  def frag = {
    div(
      div(
        "Frequency Computing Node",
        Rx {
            select(onchange := setValue(metricsRendering.wsServer))(
            optionHint("select"),
            makeOptions(viewData.frequencyServers(), metricsRendering.wsServer())
          )
        },
        button(onclick := {() => metricsRendering.setUrl()})("Set"),
        br,
        span(id := "per-sec")(metricsRendering.frequency)
      ), br,
      div(
        streamSelectionView("source-selection1", "canvas1"),
        streamSelectionView("source-selection2", "canvas2")
      )
    )
  }

  private def streamSelectionView(selectionId: String, canvasId: String) = {
    val imageRendering = new ImageRendering(viewData)
    val cvs = canvas(id := canvasId, widthA := CanvasWidth, heightA := CanvasHeight).render
    imageRendering.drawOn(cvs)
    div(
      "Image Source",
      Rx {
        select(id := selectionId, onchange := setValue(imageRendering.wsServer))(
          optionHint("select"),
          makeOptions(viewData.imageServers(), imageRendering.wsServer())
        )
      },
      button(onclick := {() => imageRendering.setUrl()})("Set"),
      cvs
    )(float := "left", width := s"${CanvasWidth + 50}px", height := s"${CanvasHeight + 50}px")
  }
}
