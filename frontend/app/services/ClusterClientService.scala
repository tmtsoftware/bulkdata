package services

import javax.inject.{Inject, Singleton}

import actors.ConnectionStore
import akka.actor.ActorSystem
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Publish
import akka.pattern.ask
import akka.util.Timeout
import tmt.common.Messages
import tmt.shared.Topics
import tmt.shared.models.ConnectionSet

import scala.concurrent.duration.{DurationInt, FiniteDuration}

@Singleton
class ClusterClientService @Inject()(system: ActorSystem) {

  implicit val timeout = Timeout(2.seconds)

  private val mediator = DistributedPubSub(system).mediator
  private val connectionStore = system.actorOf(ConnectionStore.props())

  def throttle(serverName: String, delay: FiniteDuration) = {
    mediator ! Publish(Topics.Throttle, Messages.UpdateDelay(serverName, delay))
  }

  def subscribe(serverName: String, topic: String) = {
    mediator ! Publish(Topics.Subscription, Messages.Subscribe(serverName, topic))
  }

  def unsubscribe(serverName: String, topic: String) = {
    mediator ! Publish(Topics.Subscription, Messages.Unsubscribe(serverName, topic))
  }

  def connections = (connectionStore ? ConnectionStore.GetConnections).mapTo[ConnectionSet]
}
