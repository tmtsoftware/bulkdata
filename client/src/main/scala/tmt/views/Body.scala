package tmt.views

import org.scalajs.dom.document
import scalatags.JsDom.all._

class Body(views: View*) {
  def attach() = {
    val modifiers = views.map(_.frag)
    document.body.appendChild(div(`class` := "row", modifiers).render)
  }
}
