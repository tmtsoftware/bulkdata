package common

import javax.inject.{Inject, Singleton}

import library.ConfigValueExtensions.RichConfigValue
import play.api.Configuration
import play.api.libs.json.Json
import tmt.shared.models.HostMappings

@Singleton
class AppSettings @Inject()(configuration: Configuration) {
  private val config = configuration.underlying
  val env = config.getString("env")

  implicit val hostMappingFormat = Json.format[HostMappings]
  val hosts = HostMappings(config.getObject("hosts").as[Map[String, String]])
  val bindings = config.getObject("bindings")
}
