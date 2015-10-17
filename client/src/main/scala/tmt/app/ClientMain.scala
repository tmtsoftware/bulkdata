package tmt.app

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport

object ClientMain extends JSApp {
  @JSExport
  override def main() = {
    new ClientAssembly().body.attach()
  }
}
