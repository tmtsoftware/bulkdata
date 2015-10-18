package tmt.shared.models

case class HostMappings(hosts: Map[String, String]) {
  def getHost(topic: String) =
    hosts.get(topic).map(host => s"ws://$host/$topic")
}

object HostMappings {
  def empty = HostMappings(Map.empty)
}
