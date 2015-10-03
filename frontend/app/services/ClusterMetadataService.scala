package services

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import akka.cluster.{MemberStatus, Cluster}

@Singleton
class ClusterMetadataService@Inject()(implicit system: ActorSystem) {
  val cluster = Cluster(system)
  
  def onlineMembers = {
    val upMembers = cluster.state.members.filter(_.status == MemberStatus.Up)
    val unreachableMembers = cluster.state.unreachable
    upMembers diff unreachableMembers
  }

  def onlineRoles = onlineMembers.flatMap(_.roles)
}
