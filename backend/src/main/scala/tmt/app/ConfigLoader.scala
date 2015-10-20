package tmt.app

import java.net.{NetworkInterface, InetAddress}

import com.typesafe.config._
import tmt.library.ConfigObjectExtensions.RichConfigObject
import collection.JavaConverters._

class ConfigLoader {

  def load(name: String, env: String) = {
    println(InetAddress.getLocalHost.getHostName)
    println(InetAddress.getLocalHost.getHostAddress)
    println("********************")
    NetworkInterface.getNetworkInterfaces.asScala.foreach { inter =>
      println("^^^^^^^^^^^^^^^^^^^^^^", inter.getName)
      inter.getInetAddresses.asScala.foreach { inet =>
        println("%%%%%%%%%%%%%%%%%%%%%%", inet.getHostName)
        println("%%%%%%%%%%%%%%%%%%%%%%", inet.getHostAddress)
      }
    }
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
