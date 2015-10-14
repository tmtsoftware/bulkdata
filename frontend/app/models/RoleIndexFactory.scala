package models

import javax.inject.Singleton

import com.typesafe.config.ConfigObject
import library.ConfigValueExtensions
import play.api.libs.json.JsObject
import ConfigValueExtensions.RichConfigValue
import tmt.shared.models.{RoleMapping, Role, RoleIndex}

@Singleton
class RoleIndexFactory {
  def fromConfig(bindings: ConfigObject) = RoleIndex {
    val entries = bindings.as[JsObject].value.toSeq
    entries.collect { case (serverName, v: JsObject) =>
      val role = Role.withName((v \ "role").as[String])
      RoleMapping(role, serverName)
    }
  }
}
