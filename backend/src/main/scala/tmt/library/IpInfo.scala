package tmt.library

import java.io.{InputStreamReader, BufferedReader}
import java.net._

object IpInfo {

  private val url = new URL("http://checkip.amazonaws.com")

  def privateIp(env: String) = env match {
    case "prod" => InetAddress.getLocalHost.getHostAddress
    case _      => "127.0.0.1"
  }

  def externalIp(env: String) = env match {
    case "prod" =>
      val reader = new InputStreamReader(url.openStream())
      new BufferedReader(reader).readLine()
    case _      => privateIp(env)
  }
}
