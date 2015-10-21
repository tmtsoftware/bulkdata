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
import tmt.shared.models.{Connection, ConnectionSet}
import async.Async._
import scala.concurrent.ExecutionContext

import scala.concurrent.duration.{DurationInt, FiniteDuration}

@Singleton
class ClusterClientService @Inject()(roleIndexService: RoleIndexService)(implicit system: ActorSystem, ec: ExecutionContext) {

  implicit val timeout = Timeout(2.seconds)

  private val mediator = DistributedPubSub(system).mediator
  private val connectionStore = system.actorOf(ConnectionStore.props())

  def throttle(serverName: String, delay: FiniteDuration) = {
    mediator ! Publish(Topics.Throttle, Messages.UpdateDelay(serverName, delay))
  }

  def subscribe(connection: Connection) = async {
    if(await(roleIndexService.validate(connection)))
      mediator ! Publish(Topics.Subscription, Messages.Subscribe(connection))
    else
      throw new RuntimeException(
        s"${connection.server} can not subscribe ${connection.topic} due to validation failure"
      )
  }

  def unsubscribe(connection: Connection) = {
    mediator ! Publish(Topics.Subscription, Messages.Unsubscribe(connection))
  }

  def allConnections = (connectionStore ? ConnectionStore.GetConnections).mapTo[ConnectionSet]
}
