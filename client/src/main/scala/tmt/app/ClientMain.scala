package tmt.app

import org.scalajs.dom.document

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport

object ClientMain extends JSApp {
  @JSExport
  override def main() = {
    val body = new ClientAssembly().body.layout.render
    document.body.appendChild(body)
  }
}
