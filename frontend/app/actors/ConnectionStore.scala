package actors

import akka.actor.Props
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Subscribe
import akka.persistence.PersistentActor
import tmt.common.Messages
import tmt.shared.Topics
import tmt.shared.models.ConnectionSet

class ConnectionStore extends PersistentActor {

  def persistenceId = "connection-store"

  var connectionSet = ConnectionSet.empty

  val mediator = DistributedPubSub(context.system).mediator

  override def preStart() = {
    mediator !  Subscribe(Topics.Subscription, self)
  }

  def receiveRecover = {
    case event: Messages.Subscribe      => addConnection(event)
    case event: Messages.Unsubscribe    => removeConnection(event)
  }

  def receiveCommand = {
    case event: Messages.Subscribe      => persistAsync(event)(addConnection)
    case event: Messages.Unsubscribe    => persistAsync(event)(removeConnection)
    case ConnectionStore.GetConnections => sender() ! connectionSet
  }

  def addConnection(event: Messages.Subscribe) = connectionSet = connectionSet.addConnection(event.serverName, event.topic)
  def removeConnection(event: Messages.Unsubscribe) = connectionSet = connectionSet.removeConnection(event.serverName, event.topic)
}

object ConnectionStore {
  case object GetConnections

  def props() = Props(new ConnectionStore)
}
