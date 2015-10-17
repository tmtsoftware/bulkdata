package tmt.actors

import akka.actor.{Actor, ActorRef, Props}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Subscribe, Unsubscribe}
import tmt.app.{ActorConfigs, AppSettings}
import tmt.common.Messages
import tmt.shared.Topics
import tmt.shared.models.{Connection, ConnectionSet}

class SubscriptionService[T](
  appSettings: AppSettings, actorConfigs: ActorConfigs
) extends SourceActorLink[T](actorConfigs, Topics.Subscription, Topics.Connections) {

  import actorConfigs._
  def wrap(sourceLinkedRef: ActorRef) = 
    system.actorOf(Subscription.props(appSettings.binding.name, sourceLinkedRef))
}

class Subscription(serverName: String, sourceLinkedRef: ActorRef) extends Actor {
  val mediator = DistributedPubSub(context.system).mediator

  def receive = {
    case Messages.Subscribe(Connection(`serverName`, topic))   => mediator ! Subscribe(topic, sourceLinkedRef)
    case Messages.Unsubscribe(Connection(`serverName`, topic)) => mediator ! Unsubscribe(topic, sourceLinkedRef)
    case cs: ConnectionSet                                     => cs.getTopics(serverName).foreach(topic => mediator ! Subscribe(topic, sourceLinkedRef))
  }
}

object Subscription {
  def props(serverName: String, sourceLinkedRef: ActorRef) = Props(new Subscription(serverName, sourceLinkedRef))
}
