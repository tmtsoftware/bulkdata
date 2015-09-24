package tmt.app

import com.typesafe.config.{ConfigValueFactory, ConfigResolveOptions, ConfigParseOptions, ConfigFactory}
import collection.JavaConverters._

class ConfigLoader {

  def load(name: String, env: String) = {
    val config = parse(env)
    config
      .withFallback(config.getConfig(name))
      .withFallback(ConfigValueFactory.fromMap(Map("binding-name" -> name).asJava))
      .resolve()
  }

  def parse(name: String) = ConfigFactory.load(
    name,
    ConfigParseOptions.defaults(),
    ConfigResolveOptions.defaults().setAllowUnresolved(true)
  )
}
