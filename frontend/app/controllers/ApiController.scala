package controllers

import javax.inject.{Inject, Singleton}

import common.AppSettings
import models.RoleMappingsFactory
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext

@Singleton
class ApiController @Inject()(roleMappingsFactory: RoleMappingsFactory, appSettings: AppSettings)(implicit ec: ExecutionContext) extends Controller {
  def mappings() = Action {
    val roleMappings = roleMappingsFactory.fromConfig(appSettings.bindings)
    import upickle.default._
    Ok(write(roleMappings))
  }
}
