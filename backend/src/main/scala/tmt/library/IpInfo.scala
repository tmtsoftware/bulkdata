package tmt.library

import java.io.{InputStreamReader, BufferedReader}
import java.net._

object IpInfo {

  private val url = new URL("http://checkip.amazonaws.com")

  def privateIp = InetAddress.getLocalHost.getHostAddress

  def externalIp(mode: String) = mode match {
    case "prod" =>
      val reader = new InputStreamReader(url.openStream())
      new BufferedReader(reader).readLine()
    case _      => privateIp
  }
}
