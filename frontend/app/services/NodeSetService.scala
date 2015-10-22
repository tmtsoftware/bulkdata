package services

import javax.inject.{Inject, Singleton}

import common.AppSettings
import tmt.shared.models.{Connection, ItemType}
import scala.async.Async._
import scala.concurrent.ExecutionContext

@Singleton
class NodeSetService @Inject()(
  appSettings: AppSettings,
  clusterMetadataService: ClusterMetadataService)(implicit ec: ExecutionContext) {

  def onlineNodeSet = async {
    await(clusterMetadataService.nodeSet).pruneBy(clusterMetadataService.onlineRoles)
  }

  def validate(connection: Connection) = async {
    val Connection(serverName, topic) = connection
    val nodeSet = await(onlineNodeSet)
    val inputType = nodeSet.getRole(serverName).input
    val outputType = nodeSet.getRole(topic).output
    (serverName != topic) && (inputType != ItemType.Empty) && (inputType == outputType)
  }

}
