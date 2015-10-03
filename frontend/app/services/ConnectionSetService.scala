package services

import javax.inject.{Inject, Singleton}

import scala.async.Async._
import scala.concurrent.ExecutionContext

@Singleton
class ConnectionSetService @Inject()(
  clusterClientService: ClusterClientService,
  clusterMetadataService: ClusterMetadataService)(implicit ec: ExecutionContext) {

  def connectionSet = async {
    await(clusterClientService.allConnections).pruneBy(clusterMetadataService.onlineRoles)
  }
}
