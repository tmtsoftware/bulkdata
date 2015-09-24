package controllers

import javax.inject.{Inject, Singleton}

import common.AppSettings
import play.api.mvc.{Action, Controller}
import services.ClusterClientService

import scala.concurrent.duration.DurationLong

@Singleton
class StreamController @Inject()(
  appSettings: AppSettings,
  clusterClientService: ClusterClientService
) extends Controller {

  val env = appSettings.env

  def streams() = Action {
    Ok(
      views.html.streams(
        s"ws://${appSettings.hosts.accumulator1}/accumulator1",
        s"ws://${appSettings.hosts.frequency1}/frequency1",
        s"ws://${appSettings.hosts.camera1}/camera1"
      )
    )
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
