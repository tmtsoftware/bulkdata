package tmt.framework

import monifu.concurrent.Scheduler
import org.scalajs.dom.ext.Ajax
import prickle.Unpickle
import rx._
import tmt.app.ViewData

import scala.async.Async._

class ScienceImageRx(viewData: ViewData)(implicit scheduler: Scheduler) extends FormRx(viewData) {
  
  def getUrl = viewData.nodeSet().getScienceImageUrl(server())

  val imageNames = Var(Seq.empty[String])

  Obs(currentUrl, skipInitial = true) {
    async {
      val responseText = await(Ajax.get(currentUrl())).responseText
      imageNames() = Unpickle[Seq[String]].fromString(responseText).get
    }
  }
}
