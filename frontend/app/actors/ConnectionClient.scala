package actors

import akka.actor.{Props, Actor}
import models.ConnectionDataSet
import tmt.common.Messages

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
