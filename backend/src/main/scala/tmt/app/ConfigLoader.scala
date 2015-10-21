package tmt.app

import com.typesafe.config._
import tmt.library.ConfigObjectExtensions.{RichConfig, RichConfigObject}
import tmt.library.IpInfo

class ConfigLoader {

  def load(role: String, serverName: String, env: String, seedName: Option[String]) = {
    val config = parse(env)

    val (privateIp, port) = seedName match {
      case Some(seed) => (config.getString(s"$seed.hostname"), config.getInt(s"$seed.port"))
      case None       => (IpInfo.privateIp(env), 0)
    }

    val bindingConfig = ConfigFactory.empty()
      .withPair("role", role)
      .withPair("name", serverName)
      .withPair("hostname", privateIp)
      .withPair("port", Integer.valueOf(port))
      .withPair("externalIp", IpInfo.externalIp(env))

    val binding = ConfigFactory.empty().withValue("binding", bindingConfig.root())

    config.withFallback(binding).resolve()
  }

  def parse(name: String) = ConfigFactory.load(
    name,
    ConfigParseOptions.defaults(),
    ConfigResolveOptions.defaults().setAllowUnresolved(true)
  )
}
