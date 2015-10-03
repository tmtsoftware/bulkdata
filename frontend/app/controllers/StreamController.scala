package controllers

import javax.inject.{Inject, Singleton}

import common.AppSettings
import models.HostMappings
import play.api.mvc.{Action, Controller}
import services.{ClusterClientService, RoleMappingsService}
import templates.PageFactory
import upickle.default._

import scala.async.Async._
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationLong

@Singleton
class StreamController @Inject()(
  appSettings: AppSettings,
  clusterClientService: ClusterClientService,
  roleMappingsService: RoleMappingsService,
  pageFactory: PageFactory
)(implicit ec: ExecutionContext) extends Controller {

  private val hostMappings = HostMappings(appSettings.hosts)

  def streams() = Action.async {
    async {
      Ok(pageFactory.showcase(
        roleMappingsService.onlineRoleMappings,
        hostMappings,
        await(clusterClientService.connections)).render
      )
    }
  }

  def mappings() = Action {
    Ok(write(roleMappingsService.onlineRoleMappings))
  }

  def connections() = Action.async {
    async {
      val connectionDataSet = await(clusterClientService.connections)
      Ok(write(connectionDataSet))
    }
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
