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
}
