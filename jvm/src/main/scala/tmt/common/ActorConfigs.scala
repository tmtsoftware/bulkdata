package tmt.common

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.Config

class ActorConfigs(implicit val system: ActorSystem) {
  implicit val mat = ActorMaterializer()
  implicit val ec = system.dispatcher
  val configs = new AppConfigs(system.settings.config)
}

object ActorConfigs {
  def from(name: String) = new ActorConfigs()(ActorSystem(name))
}

class AppConfigs(config: Config) {
  val framesInputDir = config.getString("data-location.frames.input")
  val framesOutputDir = config.getString("data-location.frames.output")

  val moviesInputDir = config.getString("data-location.movies.input")
  val moviesOutputDir = config.getString("data-location.movies.output")
}
