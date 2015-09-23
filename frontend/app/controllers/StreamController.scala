package controllers

import javax.inject.{Inject, Singleton}

import common.AppSettings
import play.api.mvc.{Action, Controller}
import services.ThrottlingService

import scala.concurrent.duration.DurationLong

@Singleton
class StreamController @Inject()(appSettings: AppSettings, throttlingService: ThrottlingService) extends Controller {

  val env = appSettings.env

  def streams() = Action {
    Ok(
      views.html.streams(
        s"ws://${appSettings.hosts.metricsAgg}/metrics-cumulative",
        s"ws://${appSettings.hosts.metricsAgg}/metrics-per-sec",
        s"ws://${appSettings.hosts.imageSource}/image-source"
      )
    )
  }

  def throttle(topic: String, delay: Long) = Action {
    throttlingService.throttle(s"throttle-$topic", delay.millis)
    Accepted("ok")
  }
}
