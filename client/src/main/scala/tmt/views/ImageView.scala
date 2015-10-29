package tmt.views

import monifu.concurrent.Scheduler
import rx.core.Rx
import tmt.app.ViewData
import tmt.framework.Helpers._
import tmt.framework.Framework._
import tmt.images.ImageRendering

import scalatags.JsDom.all._

class ImageView(viewData: ViewData)(implicit scheduler: Scheduler) extends View {

  import tmt.common.Constants._

  val cvs = canvas(widthA := CanvasWidth, heightA := CanvasHeight).render

  val imageRendering = new ImageRendering(cvs, viewData)

  def viewTitle = imageRendering.title

  def viewContent = cvs

  def viewAction = makeForm(viewData.imageServers, imageRendering)
}
