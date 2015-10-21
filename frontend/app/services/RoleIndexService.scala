package services

import javax.inject.{Inject, Singleton}

import common.AppSettings
import tmt.shared.models.{Connection, ItemType}
import scala.async.Async._
import scala.concurrent.ExecutionContext

@Singleton
class RoleIndexService @Inject()(
  appSettings: AppSettings,
  clusterMetadataService: ClusterMetadataService)(implicit ec: ExecutionContext) {

  def onlineRoleIndex = async {
    await(clusterMetadataService.clusterNodes).pruneBy(clusterMetadataService.onlineRoles)
  }

  def validate(connection: Connection) = async {
    val Connection(serverName, topic) = connection
    val serverNameIndex = await(onlineRoleIndex).serverNameIndex
    val inputType = serverNameIndex.getRole(serverName).input
    val outputType = serverNameIndex.getRole(topic).output
    (serverName != topic) && (inputType != ItemType.Empty) && (inputType == outputType)
  }

}
