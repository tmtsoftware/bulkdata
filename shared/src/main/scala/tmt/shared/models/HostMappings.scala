package tmt.shared.models

case class HostMappings(hosts: Map[String, String]) {
  def getHost(topic: String) = {
    val host = hosts.getOrElse(topic, "unknown")
    s"ws://$host/$topic"
  }
}

object HostMappings {
  def empty = HostMappings(Map.empty)
}
