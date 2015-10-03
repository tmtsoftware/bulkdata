package controllers

import javax.inject.{Inject, Singleton}

import common.AppSettings
import models.HostMappings
import play.api.mvc.{Action, Controller}
import services.{ConnectionSetService, ClusterClientService, RoleMappingsService}
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
  pageFactory: PageFactory,
  connectionSetService: ConnectionSetService
)(implicit ec: ExecutionContext) extends Controller {

  private val hostMappings = HostMappings(appSettings.hosts)

  def streams() = Action.async {
    async {
      Ok(pageFactory.showcase(
        roleMappingsService.onlineRoleMappings,
        hostMappings,
        await(connectionSetService.connectionSet)).render
      )
    }
  }

  def mappings() = Action {
    Ok(write(roleMappingsService.onlineRoleMappings))
  }

  def connections() = Action.async {
    async {
      val connectionDataSet = await(connectionSetService.connectionSet)
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
