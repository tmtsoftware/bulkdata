package common

import javax.inject.{Singleton, Inject}

import play.api.Configuration

@Singleton
class AppSettings @Inject()(configuration: Configuration) {
  private val config = configuration.underlying
  val env = config.getString("env")
  val hosts = config.getConfig("hosts")
}
