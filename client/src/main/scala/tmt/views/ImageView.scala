package tmt.views

import monifu.concurrent.Scheduler
import rx.core.Rx
import tmt.app.ViewData
import tmt.framework.Helpers._
import tmt.images.ImageRendering

import scalatags.JsDom.all._
import tmt.framework.Framework._

class ImageView(viewData: ViewData)(implicit scheduler: Scheduler) extends View {

  import tmt.common.Constants._

  val imageRendering = new ImageRendering(viewData)

  val cvs = canvas(widthA := CanvasWidth, heightA := CanvasHeight).render

  imageRendering.drawOn(cvs)
  imageRendering.selectedServer() = "Wavefront"

  def viewTitle = Rx(h5(imageRendering.selectedServer().capitalize))

  def viewContent = cvs

  def viewAction = makeForm(viewData.imageServers, imageRendering)
}
