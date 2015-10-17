package tmt.views

import monifu.concurrent.Scheduler
import rx._
import tmt.app.ViewData
import tmt.framework.Framework._
import tmt.framework.Helpers._
import tmt.images.ImageRendering
import tmt.metrics.MetricsRendering

import scalatags.JsDom.all._

class StreamView(dataStore: ViewData)(implicit scheduler: Scheduler) extends View {

  import tmt.common.Constants._
  val metricsRendering = new MetricsRendering

  def frag = {
    div(
      div(
        "Frequency Computing Node",
        Rx {
            select(onchange := setValue(metricsRendering.frequencyNode))(
            optionHint("select"),
            makeOptions2(dataStore.frequencyWsUrls(), dataStore.frequencyServers(), metricsRendering.frequencyNode())
          )
        },
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
    val ImageRendering = new ImageRendering
    val cvs = canvas(id := canvasId, widthA := CanvasWidth, heightA := CanvasHeight).render
    ImageRendering.drawOn(cvs)
    div(
      "Image Source",
      Rx {
        select(id := selectionId, onchange := setValue(ImageRendering.imageNode))(
          optionHint("select"),
          makeOptions2(dataStore.imageWsUrls(), dataStore.imageServers(), ImageRendering.imageNode())
        )
      },
      cvs
    )(float := "left", width := s"${CanvasWidth + 50}px", height := s"${CanvasHeight + 50}px")
  }
}
