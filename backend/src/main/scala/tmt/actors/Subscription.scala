package tmt.actors

import akka.actor.{Actor, ActorRef, Props}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Subscribe, Unsubscribe}
import tmt.app.{ActorConfigs, AppSettings}
import tmt.common.Topics
import tmt.common.models.Messages

class SubscriptionService[T](
  appSettings: AppSettings, actorConfigs: ActorConfigs
) extends ClusterReceptionistService[T](Topics.Subscription, actorConfigs) {
  import actorConfigs._
  def wrap(ref: ActorRef) = system.actorOf(Subscription.props(appSettings.binding.name, ref))
}

class Subscription(serverName: String, actorRef: ActorRef) extends Actor {
  private val mediator = DistributedPubSub(context.system).mediator

  def receive = {
    case Messages.Subscribe(`serverName`, topic)   => mediator ! Subscribe(topic, actorRef)
    case Messages.Unsubscribe(`serverName`, topic) => mediator ! Unsubscribe(topic, actorRef)
  }
}

object Subscription {
  def props(serverName: String, actorRef: ActorRef) = Props(new Subscription(serverName, actorRef))
}
