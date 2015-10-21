package tmt.app

import com.typesafe.config._
import tmt.library.ConfigObjectExtensions.RichConfigObject

class ConfigLoader {

  def load(name: String, env: String) = {
    val config = parse(env)
    val binding = ConfigFactory.empty().withValue(
      "binding", config.getObject(s"bindings.$name").withPair("name", name)
    )
    config.withFallback(binding).resolve()
  }

  def parse(name: String) = ConfigFactory.load(
    name,
    ConfigParseOptions.defaults(),
    ConfigResolveOptions.defaults().setAllowUnresolved(true)
  )
}
