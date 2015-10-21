package services

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import akka.cluster.{Cluster, MemberStatus}
import akka.cluster.ddata.DistributedData
import akka.cluster.ddata.Replicator.{Get, GetSuccess, ReadLocal}
import akka.pattern.ask
import akka.util.Timeout
import tmt.common.Keys
import tmt.shared.models.RoleIndex

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

@Singleton
class ClusterMetadataService@Inject()(implicit system: ActorSystem, ec: ExecutionContext) {
  val cluster = Cluster(system)
  val replicator = DistributedData(system).replicator
  implicit val timeout = Timeout(1.second)

  def onlineMembers = {
    val upMembers = cluster.state.members.filter(_.status == MemberStatus.Up)
    val unreachableMembers = cluster.state.unreachable
    upMembers diff unreachableMembers
  }

  def onlineRoles = onlineMembers.flatMap(_.roles)

  def clusterNodes = {
    (replicator ? Get(Keys.Nodes, ReadLocal)).collect {
      case g @ GetSuccess(Keys.Nodes, _) => RoleIndex(g.get(Keys.Nodes).entries.values.toSeq)
    }
  }
}
