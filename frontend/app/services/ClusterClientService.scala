package services

import javax.inject.{Singleton, Inject}

import akka.actor.ActorSystem
import akka.cluster.client.{ClusterClientSettings, ClusterClient}
import tmt.common.Topics
import tmt.common.models.Messages

import scala.concurrent.duration.FiniteDuration

@Singleton
class ClusterClientService @Inject()(system: ActorSystem) {

  private val client = system.actorOf(ClusterClient.props(ClusterClientSettings(system)))

  def throttle(serverName: String, delay: FiniteDuration) = {
    client ! ClusterClient.Publish(Topics.Throttle, Messages.UpdateDelay(serverName, delay))
  }

  def subscribe(serverName: String, topic: String) = {
    client ! ClusterClient.Publish(Topics.Subscription, Messages.Subscribe(serverName, topic))
  }

  def unsubscribe(serverName: String, topic: String) = {
    client ! ClusterClient.Publish(Topics.Subscription, Messages.Unsubscribe(serverName, topic))
  }

}
