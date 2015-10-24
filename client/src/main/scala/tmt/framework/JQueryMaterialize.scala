package tmt.framework

import org.scalajs.jquery.JQuery

import scala.language.implicitConversions
import scala.scalajs.js

@js.native
trait JQueryMaterialize extends JQuery {
  def material_select(): this.type = js.native
  def material_select(b: String): this.type = js.native
}

object JQueryMaterialize {
  implicit def jq2Materialize(jq: JQuery): JQueryMaterialize =
    jq.asInstanceOf[JQueryMaterialize]
}
