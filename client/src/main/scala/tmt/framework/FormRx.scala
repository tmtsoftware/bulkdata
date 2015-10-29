package tmt.framework

import rx._
import tmt.app.ViewData

abstract class FormRx(name: String, viewData: ViewData) {

  def getUrl: Option[String]

  val server: Var[String] = Var("")
  val selectedServer = Var(name)
  val currentUrl = Var("")

  def action() = getUrl.foreach { url =>
    selectedServer() = server()
    currentUrl() = url
  }

  Obs(viewData.diffs) {
    if (viewData.diffs() contains server()) {
      action()
    }
  }
}
