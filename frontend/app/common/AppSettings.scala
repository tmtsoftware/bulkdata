package common

import javax.inject.{Singleton, Inject}

import play.api.Configuration

@Singleton
class AppSettings @Inject()(configuration: Configuration) {

  private val config = configuration.underlying

  val env = config.getString("env")

  object hosts {
    val frontend = config.getString("hosts.frontend")
    val imageSource = config.getString("hosts.image-source")
    val metricsAgg = config.getString("hosts.metrics-agg")
  }
}
