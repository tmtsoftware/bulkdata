package controllers

import javax.inject.{Inject, Singleton}

import common.AppSettings
import play.api.mvc.{Action, Controller}
import prickle._
import services.{ClusterClientService, ConnectionSetService, RoleMappingsService}
import templates.Page

import scala.async.Async._
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationLong

@Singleton
class StreamController @Inject()(
  appSettings: AppSettings,
  clusterClientService: ClusterClientService,
  roleMappingsService: RoleMappingsService,
  connectionSetService: ConnectionSetService
)(implicit ec: ExecutionContext) extends Controller {

  def streams() = Action {
    Ok(new Page("showcase").render)
  }

  def mappings() = Action {
    Ok(Pickle.intoString(roleMappingsService.onlineRoleMappings))
  }

  def hosts() = Action {
    Ok(Pickle.intoString(appSettings.hosts))
  }

  def connections() = Action.async {
    async {
      val connectionDataSet = await(connectionSetService.connectionSet)
      Ok(Pickle.intoString(connectionDataSet))
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
