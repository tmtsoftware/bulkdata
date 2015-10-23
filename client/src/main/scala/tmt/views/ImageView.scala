package tmt.views

import monifu.concurrent.Scheduler
import tmt.app.ViewData
import tmt.framework.Framework._
import tmt.framework.Helpers._
import tmt.images.ImageRendering

import scalatags.JsDom.all._

class ImageView(viewData: ViewData)(implicit scheduler: Scheduler) extends View {

  import tmt.common.Constants._

  def frag = {
    div(cls := "col-lg-5")(
      streamSelectionView(),
      streamSelectionView()
    )
  }

  private def streamSelectionView() = {
    val imageRendering = new ImageRendering(viewData)
    val cvs = canvas(widthA := CanvasWidth, heightA := CanvasHeight).render
    imageRendering.drawOn(cvs)

    div(
      formGroup(cls := "row")(
        label("Select wavefront"),
        makeSelection(viewData.imageServers, imageRendering.wsServer),
        formControl(button)(onclick := { () => imageRendering.setUrl() })("Set")
      ),
      div(cls := "row")(cvs)
    )
  }
}
