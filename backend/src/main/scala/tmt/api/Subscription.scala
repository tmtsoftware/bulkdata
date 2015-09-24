package tmt.api

import akka.actor.{Props, Actor, ActorSystem, ActorRef}
import akka.cluster.client.ClusterClientReceptionist
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Unsubscribe, Subscribe}
import akka.stream.scaladsl.Sink
import tmt.app.{AppSettings, ActorConfigs}
import tmt.common.models.Messages
import tmt.library.Connector

class SubscriptionService[T](actorConfigs: ActorConfigs, appSettings: AppSettings) {
  import actorConfigs._
  private val (actorRef, _source) =  Connector.coupling(Sink.fanoutPublisher[T](2, 2))
  val linker = system.actorOf(Subscription.props(appSettings.bindingName, actorRef, system))
  ClusterClientReceptionist(system).registerSubscriber("subscribe", linker)

  def source = _source
}

class Subscription(serverName: String, actorRef: ActorRef, system: ActorSystem) extends Actor {

  private val mediator = DistributedPubSub(system).mediator

  def receive = {
    case Messages.Subscribe(`serverName`, topic) => mediator ! Subscribe(topic, actorRef)
    case Messages.Unsubscribe(`serverName`, topic) => mediator ! Unsubscribe(topic, actorRef)
  }
}

object Subscription {
  def props(serverName: String, actorRef: ActorRef, system: ActorSystem) = Props(new Subscription(serverName, actorRef, system))
}
