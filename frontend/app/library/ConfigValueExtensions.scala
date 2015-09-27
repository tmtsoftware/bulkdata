package library

import com.typesafe.config.{ConfigRenderOptions, ConfigValue}
import play.api.libs.json.{Json, Reads}

object ConfigValueExtensions {

  implicit class RichConfigValue(val configValue: ConfigValue) extends AnyVal {
    def as[T: Reads] = Json.parse(configValue.render(ConfigRenderOptions.concise())).as[T]
  }
}
