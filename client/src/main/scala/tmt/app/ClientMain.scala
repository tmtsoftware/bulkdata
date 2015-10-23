package tmt.app

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import scalatags.JsDom.all._
import org.scalajs.dom.document

object ClientMain extends JSApp {
  @JSExport
  override def main() = {
    val body = div(new ClientAssembly().mainView.frag).render
    document.body.appendChild(body)
  }
}
