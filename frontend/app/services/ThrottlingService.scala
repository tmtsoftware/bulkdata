package services

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import akka.cluster.client.{ClusterClientSettings, ClusterClient}
import tmt.common.models.Throttle

import scala.concurrent.duration.FiniteDuration

@Singleton
class ThrottlingService @Inject()(system: ActorSystem) {
  private val client = system.actorOf(ClusterClient.props(ClusterClientSettings(system)))
  def throttle(topic: String, delay: FiniteDuration) = {
    client ! ClusterClient.Publish(topic, Throttle.UpdateDelay(delay))
  }
}
