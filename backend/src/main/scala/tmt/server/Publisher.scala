package tmt.server

import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Publish
import akka.stream.scaladsl.Source
import tmt.app.ActorConfigs

class Publisher(actorConfigs: ActorConfigs) {
  import actorConfigs._

  private val mediator = DistributedPubSub(system).mediator

  def publish(topic: String, xs: Source[Any, Any]) = xs.runForeach { x =>
//    println(s"publishing: $topic: $x")
    mediator ! Publish(topic, x)
  }
}
