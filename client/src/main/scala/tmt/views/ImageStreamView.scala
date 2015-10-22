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
    div(`class` := "col-lg-8")(
      textAlign := "center",
      streamSelectionView(),
      streamSelectionView()
    )
  }

  private def streamSelectionView() = {
    val imageRendering = new ImageRendering(viewData)
    val cvs = canvas(widthA := CanvasWidth, heightA := CanvasHeight)(backgroundColor := "#f1f1f1").render
    val canvasLabel = label().render

    imageRendering.drawOn(cvs)
    div(
      Rx {
        makeOptions("select", viewData.imageServers(), imageRendering.wsServer, "100px")
      },
      button(`class` := "btn btn-default active")(
        onclick := {() => setSource(imageRendering, canvasLabel)}
      )("Set"),
      canvasLabel,
      br,
      br,
      cvs,
      br,
      br
    )
  }
}
