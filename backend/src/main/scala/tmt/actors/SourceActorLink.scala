package tmt.actors

import akka.actor.ActorRef
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Subscribe
import akka.stream.scaladsl.Sink
import tmt.app.ActorConfigs
import tmt.library.Connector

abstract class SourceActorLink[T](actorConfigs: ActorConfigs, topics: String*) {
  def wrap(sourceLinkedRef: ActorRef): ActorRef

  import actorConfigs._
  private val (sourceLinkedRef, _source) = Connector.coupling[T](Sink.publisher)
  private val mediator = DistributedPubSub(system).mediator
  val wrappedActor = wrap(sourceLinkedRef)
  topics.foreach(topic => mediator ! Subscribe(topic, wrappedActor))

  def source = _source
}
