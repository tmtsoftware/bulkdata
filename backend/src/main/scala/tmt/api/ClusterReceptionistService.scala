package tmt.api

import akka.actor.ActorRef
import akka.cluster.client.ClusterClientReceptionist
import akka.stream.scaladsl.Sink
import tmt.app.ActorConfigs
import tmt.library.Connector

abstract class ClusterReceptionistService[T](topic: String, actorConfigs: ActorConfigs) {
  def wrap(ref: ActorRef): ActorRef

  import actorConfigs._
  private val (actorRef, _source) = Connector.coupling[T](Sink.publisher)
  ClusterClientReceptionist(system).registerSubscriber(topic, wrap(actorRef))

  def source = _source
}
