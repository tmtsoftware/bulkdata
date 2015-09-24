package tmt.server

import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe, Unsubscribe}
import akka.stream.scaladsl.{Sink, Source}
import library.Role
import tmt.app.ActorConfigs
import tmt.library.Connector

class Publisher(actorConfigs: ActorConfigs) {
  import actorConfigs._

  private val mediator = DistributedPubSub(system).mediator

  def publish(role: Role, xs: Source[Any, Any]) = xs.runForeach { x =>
    if(role != Role.Accumulator) {
      println(s"publishing: $role: $x")
    }
    mediator ! Publish(role.entryName, x)
  }
}

class Subscriber[T](actorConfigs: ActorConfigs) {
  import actorConfigs._

  private val mediator = DistributedPubSub(system).mediator

  private val (actorRef, _source) =  Connector.coupling(Sink.fanoutPublisher[T](2, 2))

  def source = _source
  def subscribe(role: Role) = mediator ! Subscribe(role.entryName, actorRef)
  def unsubscribe(role: Role) = mediator ! Unsubscribe(role.entryName, actorRef)
}
