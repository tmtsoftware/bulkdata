package models

import javax.inject.Singleton

import com.typesafe.config.ConfigObject
import library.ConfigValueExtensions
import play.api.libs.json.JsObject
import ConfigValueExtensions.RichConfigValue
import tmt.shared.models.{RoleMapping, Role, RoleMappingSet}

@Singleton
class RoleMappingsFactory {
  def fromConfig(bindings: ConfigObject) = RoleMappingSet {
    val entries = bindings.as[JsObject].value.toSeq
    entries
      .collect {
        case (k, v: JsObject) => RoleMapping(Role.withName((v \ "role").as[String]), k)
      }
      .groupBy(_.role)
      .mapValues(_.map(_.server))
  }
}
