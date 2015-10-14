package services

import javax.inject.{Inject, Singleton}

import common.AppSettings
import models.RoleIndexFactory

@Singleton
class RoleIndexService @Inject()(
  roleIndexFactory: RoleIndexFactory,
  appSettings: AppSettings,
  clusterMetadataService: ClusterMetadataService) {

  private val roleIndex = roleIndexFactory.fromConfig(appSettings.bindings)
  def onlineRoleIndex = roleIndex.pruneBy(clusterMetadataService.onlineRoles)

  def validate(serverName: String, topic: String) = {
    if(serverName == topic) {
      false
    }
    else {
      for {
        inputRole <- onlineRoleIndex.serverNameIndex.getRole(serverName)
        inputType <- inputRole.maybeInput
        outRole <- onlineRoleIndex.serverNameIndex.getRole(topic)
        outType <- outRole.maybeOutput
      } yield outType == inputType
    }.getOrElse(false)
  }

}
