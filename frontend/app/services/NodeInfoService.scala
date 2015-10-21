package services

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import akka.cluster.Cluster
import akka.cluster.ddata.DistributedData
import akka.cluster.ddata.Replicator.{GetSuccess, ReadLocal, Get}
import akka.pattern.ask
import akka.util.Timeout
import tmt.common.Keys

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

@Singleton
class NodeInfoService @Inject()(implicit system: ActorSystem, ec: ExecutionContext) {

  val replicator = DistributedData(system).replicator
  implicit val cluster = Cluster(system)
  implicit val timeout = Timeout(1.second)

  (replicator ? Get(Keys.Nodes, ReadLocal)).collect {
    case g @ GetSuccess(Keys.Nodes, _) =>
      val value = g.get(Keys.Nodes).entries
  }

}
