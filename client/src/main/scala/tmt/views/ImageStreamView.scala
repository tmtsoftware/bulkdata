package tmt.views

import monifu.concurrent.Scheduler
import rx._
import tmt.app.ViewData
import tmt.css.Styles
import tmt.framework.Framework._
import tmt.framework.Helpers._
import tmt.images.ImageRendering

import scalatags.JsDom.all._

class ImageStreamView(viewData: ViewData)(implicit scheduler: Scheduler) extends View {

  import tmt.common.Constants._
  def frag = {
    div(`class` := "col-lg-8")(
      streamSelectionView(),
      streamSelectionView()
    )(Styles.centerAlign)
  }

  private def streamSelectionView() = {
    val imageRendering = new ImageRendering(viewData)
    val cvs = canvas(widthA := CanvasWidth, heightA := CanvasHeight)(Styles.canvas).render
    val canvasLabel = label().render

    imageRendering.drawOn(cvs)
    div(
      Rx {
        makeOptions("select", viewData.imageServers(), imageRendering.wsServer, "100px")
      },
      defaultButton("Set", _ => setSource(imageRendering, canvasLabel)),
      canvasLabel,
      br,
      cvs,
      br,
      br
    )
  }
}
