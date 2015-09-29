package controllers

import javax.inject.{Inject, Singleton}

import common.AppSettings
import models.{RoleMappingsFactory, HostMappings}
import play.api.mvc.{Action, Controller}
import services.ClusterClientService
import templates.PageFactory

import scala.concurrent.duration.DurationLong

@Singleton
class StreamController @Inject()(
  appSettings: AppSettings,
  clusterClientService: ClusterClientService,
  roleMappingsFactory: RoleMappingsFactory,
  pageFactory: PageFactory
) extends Controller {

  private val roleMappings = roleMappingsFactory.fromConfig(appSettings.bindings)
  private val hostMappings = HostMappings(appSettings.hosts)

  def streams() = Action {
    Ok(pageFactory.showcase(roleMappings, hostMappings).render)
  }

  def mappings() = Action {
    import upickle.default._
    Ok(write(roleMappings))
  }

  def throttle(serverName: String, delay: Long) = Action {
    clusterClientService.throttle(serverName, delay.millis)
    Accepted("ok")
  }

  def subscribe(serverName: String, topic: String) = Action {
    clusterClientService.subscribe(serverName, topic)
    Accepted("ok")
  }

  def unsubscribe(serverName: String, topic: String) = Action {
    clusterClientService.unsubscribe(serverName, topic)
    Accepted("ok")
  }
}
