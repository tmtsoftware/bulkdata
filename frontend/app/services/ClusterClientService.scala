package services

import javax.inject.{Inject, Singleton}

import actors.ConnectionStore
import akka.actor.ActorSystem
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Publish
import akka.pattern.ask
import akka.util.Timeout
import tmt.common.Messages
import tmt.library.Role
import tmt.shared.Topics
import tmt.shared.models.ConnectionSet

import scala.concurrent.duration.{DurationInt, FiniteDuration}
import scala.util.{Failure, Success, Try}

@Singleton
class ClusterClientService @Inject()(system: ActorSystem, roleMappingsService: RoleMappingsService) {

  implicit val timeout = Timeout(2.seconds)

  private val mediator = DistributedPubSub(system).mediator
  private val connectionStore = system.actorOf(ConnectionStore.props())

  def throttle(serverName: String, delay: FiniteDuration) = {
    mediator ! Publish(Topics.Throttle, Messages.UpdateDelay(serverName, delay))
  }

  def subscribe(serverName: String, topic: String): Try[Unit] = {
    validate(serverName, topic) match {
      case Some(true) =>
        Success(mediator ! Publish(Topics.Subscription, Messages.Subscribe(serverName, topic)))
      case _          =>
        Failure(new ValidationFailedException("Bad request"))
    }
  }

  def unsubscribe(serverName: String, topic: String) = {
    mediator ! Publish(Topics.Subscription, Messages.Unsubscribe(serverName, topic))
  }

  def allConnections = (connectionStore ? ConnectionStore.GetConnections).mapTo[ConnectionSet]

  private def validate(sourceServerName: String, destinationServerName: String) = {
    val roleMappings = roleMappingsService.onlineRoleMappings

    for {
      sourceRoleName <- roleMappings.roleOf(sourceServerName)
      destinationRoleName <- roleMappings.roleOf(destinationServerName)
      sourceRole = Role.withName(sourceRoleName)
      destinationRole = Role.withName(destinationRoleName)
    } yield sourceRole.maybeConsumes.isDefined && sourceRole.maybeConsumes == destinationRole.maybeProduces
  }
}

case class ValidationFailedException(msg: String) extends RuntimeException(msg)
