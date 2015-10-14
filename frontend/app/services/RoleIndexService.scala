package services

import javax.inject.{Inject, Singleton}

import common.AppSettings
import models.RoleIndexFactory
import tmt.shared.models.ItemType

@Singleton
class RoleIndexService @Inject()(
  roleIndexFactory: RoleIndexFactory,
  appSettings: AppSettings,
  clusterMetadataService: ClusterMetadataService) {

  private val roleIndex = roleIndexFactory.fromConfig(appSettings.bindings)

  def onlineRoleIndex = roleIndex.pruneBy(clusterMetadataService.onlineRoles)

  def validate(serverName: String, topic: String) = {
    lazy val inputType = onlineRoleIndex.serverNameIndex.getRole(serverName).input
    lazy val outputType = onlineRoleIndex.serverNameIndex.getRole(topic).output
    (serverName != topic) && (inputType != ItemType.Empty) && (inputType == outputType)
  }

}
