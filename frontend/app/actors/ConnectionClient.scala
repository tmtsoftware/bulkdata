package actors

import akka.actor.{Props, Actor}
import tmt.common.Messages
import tmt.shared.models.ConnectionDataSet

class ConnectionClient extends Actor {

  var connections = Map.empty[String, Set[String]]

  def receive = {
    case Messages.ConnectionData(cs)     => connections = cs
    case ConnectionClient.GetConnections => sender() ! ConnectionDataSet(connections)
  }
}

object ConnectionClient {
  case object GetConnections

  def props() = Props(new ConnectionClient)
}
