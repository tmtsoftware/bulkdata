package controllers

import javax.inject.{Inject, Singleton}

import common.AppSettings
import play.api.mvc.{Action, Controller}
import services.ClusterClientService

import scala.concurrent.duration.DurationLong

@Singleton
class StreamController @Inject()(appSettings: AppSettings, clusterClientService: ClusterClientService) extends Controller {

  def streams() = Action {
    Ok(
      views.html.streams(
        makeUrl("accumulator1"),
        makeUrl("frequency1"),
        makeUrl("camera1")
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
}
