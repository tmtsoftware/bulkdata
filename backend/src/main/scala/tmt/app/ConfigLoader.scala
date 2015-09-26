package tmt.app

import com.typesafe.config.{ConfigValueFactory, ConfigResolveOptions, ConfigParseOptions, ConfigFactory}
import collection.JavaConverters._

class ConfigLoader {

  def load(name: String, env: String) = {
    val config = parse(env)
    val binding = Map(
      "binding" -> config.getObject(s"bindings.$name"),
      "binding-name" -> name
    )
    val bindingConfig = ConfigValueFactory.fromMap(binding.asJava)
    config.withFallback(bindingConfig).resolve()
  }

  def parse(name: String) = ConfigFactory.load(
    name,
    ConfigParseOptions.defaults(),
    ConfigResolveOptions.defaults().setAllowUnresolved(true)
  )
}
