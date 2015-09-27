package controllers

import javax.inject.{Inject, Singleton}

import common.AppSettings
import library.GenericExtensions.RichGeneric
import models.{RoleMappings, HostMappings}
import play.api.mvc.{Action, Controller}
import services.ClusterClientService

import scala.concurrent.duration.DurationLong

@Singleton
class StreamController @Inject()(
  appSettings: AppSettings,
  clusterClientService: ClusterClientService
) extends Controller {

  def streams() = Action {
    Ok(
      views.html.streams(
        HostMappings(appSettings.hosts),
        roleMappings
      )
    )
  }

  private def makeUrl(topic: String) = {
    val host = appSettings.hosts.getString(topic)
    s"ws://$host/$topic"
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

  private val roleMappings = RoleMappings(appSettings.bindings)

  def servers(role: String) = Action {
    Ok(roleMappings.getServers(role).toJson)
  }
}
