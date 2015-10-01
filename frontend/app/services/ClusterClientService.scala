package services

import javax.inject.{Singleton, Inject}

import actors.ConnectionClient
import akka.actor.ActorSystem
import akka.cluster.client.{ClusterClientSettings, ClusterClient}
import akka.util.Timeout
import models.ConnectionDataSet
import tmt.common.Messages
import tmt.shared.Topics
import akka.pattern.ask

import scala.concurrent.duration.{DurationInt, FiniteDuration}

@Singleton
class ClusterClientService @Inject()(system: ActorSystem) {

  implicit val timeout = Timeout(2.seconds)

  private val clusterClient = system.actorOf(ClusterClient.props(ClusterClientSettings(system)))
  private val connectionClient = system.actorOf(ConnectionClient.props())

  clusterClient ! ClusterClient.Publish(Topics.Connections, Messages.Register(connectionClient))

  def throttle(serverName: String, delay: FiniteDuration) = {
    clusterClient ! ClusterClient.Publish(Topics.Throttle, Messages.UpdateDelay(serverName, delay))
  }

  def subscribe(serverName: String, topic: String) = {
    clusterClient ! ClusterClient.Publish(Topics.Subscription, Messages.Subscribe(serverName, topic))
  }

  def unsubscribe(serverName: String, topic: String) = {
    clusterClient ! ClusterClient.Publish(Topics.Subscription, Messages.Unsubscribe(serverName, topic))
  }

  def connections = (connectionClient ? ConnectionClient.GetConnections).mapTo[ConnectionDataSet]
}
