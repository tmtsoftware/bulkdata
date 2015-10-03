package actors

import akka.actor.{Actor, Props}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Subscribe
import tmt.common.Messages
import tmt.shared.Topics
import tmt.shared.models.ConnectionSet

class ConnectionStore extends Actor {

  var connectionSet = ConnectionSet.empty

  val mediator = DistributedPubSub(context.system).mediator


  override def preStart() = {
    mediator !  Subscribe(Topics.Subscription, self)
  }

  def receive = {
    case Messages.Subscribe(serverName, topic)   => connectionSet = connectionSet.addConnection(serverName, topic)
    case Messages.Unsubscribe(serverName, topic) => connectionSet = connectionSet.removeConnection(serverName, topic)
    case ConnectionStore.GetConnections          => sender() ! connectionSet
  }
}

object ConnectionStore {
  case object GetConnections

  def props() = Props(new ConnectionStore)
}
