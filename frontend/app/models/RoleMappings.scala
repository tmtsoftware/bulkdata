package models

import com.typesafe.config.ConfigObject
import library.ConfigValueExtensions.RichConfigValue
import play.api.libs.json.JsObject

case class RoleMappings(bindings: ConfigObject) {
  private val entries = bindings.as[JsObject].value.toSeq

  private val mappings = entries
    .collect {
      case (k, v: JsObject) => Mapping((v \ "role").as[String], k)
    }
    .groupBy(_.role)
    .mapValues(_.map(_.serverName))

  def getServers(role: String) = mappings.getOrElse(role, Seq.empty)

  private case class Mapping(role: String, serverName: String)
}
