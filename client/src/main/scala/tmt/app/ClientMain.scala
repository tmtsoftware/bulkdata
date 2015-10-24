package tmt.app

import org.scalajs.dom.document
import org.scalajs.jquery.jQuery
import tmt.framework.JQueryMaterialize.jq2Materialize

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport

object ClientMain extends JSApp {
  @JSExport
  override def main() = {
    val body = new ClientAssembly().body.layout.render
    document.body.appendChild(body)
    jQuery(document).ready { () =>
      jQuery("select").material_select()
    }
  }
}
