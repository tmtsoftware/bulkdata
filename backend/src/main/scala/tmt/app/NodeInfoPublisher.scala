package tmt.app

import akka.cluster.Cluster
import akka.cluster.ddata.Replicator.{Update, WriteLocal}
import akka.cluster.ddata.{DistributedData, LWWMap}
import tmt.common.Keys
import tmt.shared.models.{NodeS$, Node}

class NodeInfoPublisher(actorConfigs: ActorConfigs, appSettings: AppSettings) {

  import actorConfigs._

  val replicator = DistributedData(system).replicator
  implicit val cluster = Cluster(system)

  def publish(httpPort: Int) = replicator ! Update(Keys.Nodes, LWWMap.empty[NodeS], WriteLocal) { map =>
    map + (appSettings.binding.name -> NodeS.fromRoleMapping(appSettings.binding.roleMapping(httpPort)))
  }
}
