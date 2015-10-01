package tmt.actors

import akka.actor.{Props, ActorRef, Actor}
import akka.cluster.client.ClusterClientReceptionist
import akka.cluster.ddata.DistributedData
import akka.cluster.ddata.Replicator._
import tmt.common.Messages
import tmt.shared.Topics

class ConnectionStore extends Actor {

  val replicator = DistributedData(context.system).replicator

  var connectionClient = ActorRef.noSender

  override def preStart() = {
    ClusterClientReceptionist(context.system).registerSubscriber(Topics.Connections, self)
    replicator ! Subscribe(Subscription.DataKey, self)
  }

  def receive = {
    case Messages.Register(ref)          =>
      connectionClient = ref
    case g@Changed(Subscription.DataKey) =>
      val entries = g.get(Subscription.DataKey).entries
      val sanitizedEntries = entries.map { case (k, vs) => k -> vs.toSet }
      connectionClient ! Messages.ConnectionData(sanitizedEntries)
    case x                               =>
      println(s"unhandled messages****************: $x")
  }
}

object ConnectionStore {
  def props() = Props(new ConnectionStore)
}
