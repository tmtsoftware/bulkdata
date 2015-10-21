package tmt.views

import monifu.concurrent.Scheduler
import rx._
import tmt.app.ViewData
import tmt.framework.Framework._
import tmt.framework.Helpers._
import tmt.images.ImageRendering

import scalatags.JsDom.all._

class ImageStreamView(viewData: ViewData)(implicit scheduler: Scheduler) extends View {

  import tmt.common.Constants._
  def frag = {
    div(`class` := "col-lg-8", textAlign := "center",
      streamSelectionView("source-selection1", "canvas1"),
      streamSelectionView("source-selection2", "canvas2")
    )
  }

  private def streamSelectionView(selectionId: String, canvasId: String) = {
    val imageRendering = new ImageRendering(viewData)
    val cvs = canvas(id := canvasId, widthA := CanvasWidth, heightA := CanvasHeight)(backgroundColor := "#f1f1f1").render
    val canvasLabel = label().render

    imageRendering.drawOn(cvs)
    div(
      Rx{ makeOptions("select", viewData.imageServers(), imageRendering.wsServer()= _, imageRendering.wsServer()) },
      button(onclick := {() => setSource(imageRendering, canvasLabel)}, `class` := "btn btn-default active")("Set"),
      canvasLabel,
      br,
      br,
      cvs,
      br,
      br
    )
  }
}