package models

import com.typesafe.config.Config

case class HostMappings(hosts: Config) {
  def getHost(topic: String) = {
    val host = hosts.getString(topic)
    s"ws://$host/$topic"
  }
}
