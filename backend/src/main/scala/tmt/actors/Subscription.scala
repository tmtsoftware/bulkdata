package tmt.actors

import akka.actor.{Actor, ActorRef, Props}
import akka.cluster.ddata.Replicator.{WriteLocal, Update}
import akka.cluster.ddata.{ORSet, ORSetKey, DistributedData}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Subscribe, Unsubscribe}
import tmt.app.{ActorConfigs, AppSettings}
import tmt.common.Topics
import tmt.common.models.Messages

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

  val DataKey = ORSetKey[String](serverName)

  def receive = {
    case Messages.Subscribe(`serverName`, topic)   =>
      mediator ! Subscribe(topic, sourceLinkedRef)
      replicator ! Update(DataKey, ORSet.empty[String], WriteLocal)(_ + topic)
    case Messages.Unsubscribe(`serverName`, topic) =>
      mediator ! Unsubscribe(topic, sourceLinkedRef)
      replicator ! Update(DataKey, ORSet.empty[String], WriteLocal)(_ - topic)
  }
}

object Subscription {
  def props(serverName: String, sourceLinkedRef: ActorRef) = Props(new Subscription(serverName, sourceLinkedRef))
}
