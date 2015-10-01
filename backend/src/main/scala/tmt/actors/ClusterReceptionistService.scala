package tmt.actors

import akka.actor.ActorRef
import akka.cluster.client.ClusterClientReceptionist
import akka.stream.scaladsl.Sink
import tmt.app.ActorConfigs
import tmt.library.Connector

abstract class ClusterReceptionistService[T](topic: String, actorConfigs: ActorConfigs) {
  def wrap(sourceLinkedRef: ActorRef): ActorRef

  import actorConfigs._
  private val (sourceLinkedRef, _source) = Connector.coupling[T](Sink.publisher)
  ClusterClientReceptionist(system).registerSubscriber(topic, wrap(sourceLinkedRef))

  def source = _source
}
