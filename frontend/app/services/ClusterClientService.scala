package services

import javax.inject.{Singleton, Inject}

import akka.actor.ActorSystem
import akka.cluster.client.{ClusterClientSettings, ClusterClient}
import tmt.common.models.Messages

import scala.concurrent.duration.FiniteDuration

@Singleton
class ClusterClientService @Inject()(system: ActorSystem) {

  private val client = system.actorOf(ClusterClient.props(ClusterClientSettings(system)))

  def throttle(serverName: String, delay: FiniteDuration) = {
    client ! ClusterClient.Publish("throttle", Messages.UpdateDelay(serverName, delay))
  }

  def subscribe(serverName: String, topic: String) = {
    client ! ClusterClient.Publish("subscribe", Messages.Subscribe(serverName, topic))
  }

  def unsubscribe(serverName: String, topic: String) = {
    client ! ClusterClient.Publish("subscribe", Messages.Unsubscribe(serverName, topic))
  }

}
