package tmt.views

import monifu.concurrent.Scheduler
import rx._
import tmt.app.ViewData
import tmt.framework.Framework._
import tmt.framework.Helpers._
import tmt.framework.ScienceImageRx

import scalatags.JsDom.all._

class ScienceImageView(viewData: ViewData)(implicit scheduler: Scheduler) extends View {

  val scienceImageRx = new ScienceImageRx(viewData)
  scienceImageRx.selectedServer() = "Science Images"
  
  def viewTitle = Rx(h5(scienceImageRx.selectedServer().capitalize))

  def viewContent = Rx {
    div(cls := "collection")(
      scienceImageRx.imageNames().map { name =>
        a(cls := "collection-item")(href := s"${scienceImageRx.currentUrl()}/$name")(name)
      }
    )
  }

  def viewAction = makeForm(viewData.scienceImageServers, scienceImageRx)
}
