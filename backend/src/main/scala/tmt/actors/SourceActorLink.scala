package tmt.actors

import akka.actor.ActorRef
import akka.cluster.client.ClusterClientReceptionist
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Subscribe
import akka.stream.scaladsl.Sink
import tmt.app.ActorConfigs
import tmt.library.Connector

abstract class SourceActorLink[T](topic: String, actorConfigs: ActorConfigs) {
  def wrap(sourceLinkedRef: ActorRef): ActorRef

  import actorConfigs._
  private val (sourceLinkedRef, _source) = Connector.coupling[T](Sink.publisher)
  private val mediator = DistributedPubSub(system).mediator
  mediator ! Subscribe(topic, wrap(sourceLinkedRef))

  def source = _source
}
