package tmt.app

import akka.cluster.Cluster
import akka.cluster.ddata.Replicator.{Update, WriteLocal}
import akka.cluster.ddata.{DistributedData, LWWMap}
import tmt.common.Keys
import tmt.shared.models.RoleMapping

class NodeInfoPublisher(actorConfigs: ActorConfigs, appSettings: AppSettings) {

  import actorConfigs._

  val replicator = DistributedData(system).replicator
  implicit val cluster = Cluster(system)

  def publish() = replicator ! Update(Keys.Nodes, LWWMap.empty[RoleMapping], WriteLocal) { map =>
    map + (appSettings.binding.name -> appSettings.binding.roleMapping)
  }
}
