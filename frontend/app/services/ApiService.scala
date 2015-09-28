package services

import javax.inject.{Inject, Singleton}

import common.AppSettings
import models.RoleMappingsFactory
import tmt.common.api.Api

import scala.concurrent.Future

@Singleton
class ApiService @Inject()(roleMappingsFactory: RoleMappingsFactory, appSettings: AppSettings) extends Api {
  def getRoleMappings() = Future.successful(roleMappingsFactory.fromConfig(appSettings.bindings))
}
