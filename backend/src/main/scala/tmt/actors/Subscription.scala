package tmt.actors

import akka.actor.{Actor, ActorRef, Props}
import akka.cluster.Cluster
import akka.cluster.ddata.Replicator.{WriteLocal, Update}
import akka.cluster.ddata._
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Subscribe, Unsubscribe}
import tmt.app.{ActorConfigs, AppSettings}
import tmt.common.Messages
import tmt.shared.Topics

class SubscriptionService[T](
  appSettings: AppSettings, actorConfigs: ActorConfigs
) extends ClusterReceptionistService[T](Topics.Subscription, actorConfigs) {

  import actorConfigs._
  def wrap(sourceLinkedRef: ActorRef) = 
    system.actorOf(Subscription.props(appSettings.binding.name, sourceLinkedRef))
}

class Subscription(serverName: String, sourceLinkedRef: ActorRef) extends Actor {
  val mediator = DistributedPubSub(context.system).mediator
  val replicator = DistributedData(context.system).replicator
  implicit val cluster = Cluster(context.system)

  def receive = {
    case Messages.Subscribe(`serverName`, topic)   =>
      mediator ! Subscribe(topic, sourceLinkedRef)
      replicator ! Update(Subscription.DataKey, ORMultiMap.empty[String], WriteLocal)(_.addBinding(serverName, topic))
    case Messages.Unsubscribe(`serverName`, topic) =>
      mediator ! Unsubscribe(topic, sourceLinkedRef)
      replicator ! Update(Subscription.DataKey, ORMultiMap.empty[String], WriteLocal)(_.removeBinding(serverName, topic))
  }
}

object Subscription {
  def props(serverName: String, sourceLinkedRef: ActorRef) = Props(new Subscription(serverName, sourceLinkedRef))
  val DataKey = ORMultiMapKey[String](Topics.Connections)
}
