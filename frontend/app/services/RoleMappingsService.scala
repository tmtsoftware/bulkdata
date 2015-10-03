package services

import javax.inject.{Inject, Singleton}

import common.AppSettings
import models.RoleMappingsFactory

@Singleton
class RoleMappingsService @Inject()(
  roleMappingsFactory: RoleMappingsFactory,
  appSettings: AppSettings,
  clusterMetadataService: ClusterMetadataService) {

  private val roleMappings = roleMappingsFactory.fromConfig(appSettings.bindings)
  def onlineRoleMappings = roleMappings.pruneBy(clusterMetadataService.onlineRoles)
}
