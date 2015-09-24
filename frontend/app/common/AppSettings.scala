package common

import javax.inject.{Singleton, Inject}

import play.api.Configuration

@Singleton
class AppSettings @Inject()(configuration: Configuration) {

  private val config = configuration.underlying

  val env = config.getString("env")

  object hosts {
    val camera1 = config.getString("hosts.camera1")
    val camera2 = config.getString("hosts.camera2")
    val accumulator1 = config.getString("hosts.accumulator1")
    val accumulator2 = config.getString("hosts.accumulator2")
    val frequency1 = config.getString("hosts.frequency1")
    val frequency2 = config.getString("hosts.frequency2")
  }
}
