package tmt.views

import rx._
import tmt.app.{ViewData, DataStore}
import tmt.framework.Framework._
import tmt.framework.Helpers._
import tmt.images.ImageRendering
import tmt.metrics.MetricsRendering

import scalatags.JsDom.all._

class StreamView(dataStore: ViewData) {

  import tmt.common.Constants._

  def frag = {
    div(
      div(
        "Frequency Computing Node",
        Rx {
            select(onchange := setValue(MetricsRendering.frequencyNode))(
            optionHint("select"),
            makeOptions2(dataStore.frequencyWsUrls(), dataStore.frequencyServers(), MetricsRendering.frequencyNode())
          )
        },
        br,
        span(id := "per-sec")(MetricsRendering.frequency)
      ), br,
      div(
        streamSelectionView("source-selection1", "canvas1"),
        streamSelectionView("source-selection2", "canvas2")
      )
    )
  }

  private def streamSelectionView(selectionId: String, canvasId: String) = {
    val ImageRendering = new ImageRendering
    val cvs = canvas(id := canvasId, widthA := canvasWidth, heightA := canvasHeight).render
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
    )(float := "left", width := s"${canvasWidth + 50}px", height := s"${canvasHeight + 50}px")
  }
}
