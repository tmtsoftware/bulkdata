package controllers

import javax.inject.{Inject, Singleton}

import common.AppSettings
import play.api.mvc.{Action, Controller}

@Singleton
class StreamController @Inject()(appSettings: AppSettings) extends Controller {

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
}
