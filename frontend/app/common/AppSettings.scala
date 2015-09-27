package common

import javax.inject.{Inject, Singleton}

import play.api.Configuration

@Singleton
class AppSettings @Inject()(configuration: Configuration) {
  private val config = configuration.underlying
  val env = config.getString("env")
  val hosts = config.getConfig("hosts")
  val bindings = config.getObject("bindings")
}
