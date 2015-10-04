package actors

import akka.actor.Props
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe}
import akka.persistence.PersistentActor
import tmt.common.Messages
import tmt.shared.Topics
import tmt.shared.models.ConnectionSet

import scala.concurrent.duration._

class ConnectionStore extends PersistentActor {

  def persistenceId = "connection-store"

  var connectionSet = ConnectionSet.empty

  val mediator = DistributedPubSub(context.system).mediator
  val cluster = Cluster(context.system)
  val scheduler = context.system.scheduler

  import context.dispatcher

  override def preStart() = {
    cluster.subscribe(self, classOf[MemberUp])
    mediator !  Subscribe(Topics.Subscription, self)
  }

  def receiveRecover = {
    case event: Messages.Subscribe   => addConnection(event)
    case event: Messages.Unsubscribe => removeConnection(event)
  }

  def receiveCommand = {
    case event: Messages.Subscribe      => persist(event)(addConnection)
    case event: Messages.Unsubscribe    => persist(event)(removeConnection)
    case x: MemberUp                    =>
      println("******: ", x)
      scheduler.scheduleOnce(1.second, mediator, Publish(Topics.Connections, connectionSet))
    case ConnectionStore.GetConnections => sender() ! connectionSet
  }

  def addConnection(event: Messages.Subscribe) = connectionSet = connectionSet.addConnection(event.serverName, event.topic)
  def removeConnection(event: Messages.Unsubscribe) = connectionSet = connectionSet.removeConnection(event.serverName, event.topic)
}

object ConnectionStore {
  case object GetConnections

  def props() = Props(new ConnectionStore)
}
