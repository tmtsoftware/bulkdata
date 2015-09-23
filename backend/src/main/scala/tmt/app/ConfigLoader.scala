package tmt.app

import com.typesafe.config.{ConfigResolveOptions, ConfigParseOptions, ConfigFactory}

class ConfigLoader {

  def load(name: String, env: String) = {
    val config = parse(env)
    config.withFallback(config.getConfig(name)).resolve()
  }

  def parse(name: String) = ConfigFactory.load(
    name,
    ConfigParseOptions.defaults(),
    ConfigResolveOptions.defaults().setAllowUnresolved(true)
  )
}
