package tmt.library

import com.typesafe.config.{Config, ConfigObject, ConfigValueFactory}

object ConfigObjectExtensions {

  implicit class RichConfigObject(val config: ConfigObject) extends AnyVal {
    def withPair(key: String, value: AnyRef) = config.withValue(key, ConfigValueFactory.fromAnyRef(value))
  }

  implicit class RichConfig(val config: Config) extends AnyVal {
    def withPair(key: String, value: AnyRef) = config.withValue(key, ConfigValueFactory.fromAnyRef(value))
  }
}
