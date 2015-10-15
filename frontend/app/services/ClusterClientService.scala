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
import scala.concurrent.duration.{DurationLong, DurationInt}

@Singleton
class ClusterClientService @Inject()(roleIndexService: RoleIndexService)(implicit system: ActorSystem) {

  implicit val timeout = Timeout(2.seconds)

  private val mediator = DistributedPubSub(system).mediator
  private val connectionStore = system.actorOf(ConnectionStore.props())

  def throttle(serverName: String, ratePerSecond: Long) = {
    mediator ! Publish(Topics.Throttle, Messages.UpdateDelay(serverName, delay(ratePerSecond)))
  }

  def subscribe(serverName: String, topic: String) = {
    if(roleIndexService.validate(serverName, topic))
      mediator ! Publish(Topics.Subscription, Messages.Subscribe(serverName, topic))
    else
      throw new RuntimeException(s"$serverName can not subscribe $topic due to validation failure")
  }

  def unsubscribe(serverName: String, topic: String) = {
    mediator ! Publish(Topics.Subscription, Messages.Unsubscribe(serverName, topic))
  }

  def allConnections = (connectionStore ? ConnectionStore.GetConnections).mapTo[ConnectionSet]

  private def delay(ratePerSecond: Long) = {
    (1000/ratePerSecond).millis
  }
}
