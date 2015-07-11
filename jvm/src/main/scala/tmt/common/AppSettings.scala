package tmt.common

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri

class AppSettings(actorConfigs: ActorConfigs) {

  import actorConfigs._

  val config = actorConfigs.system.settings.config
  val superPool = Http().superPool[Uri]()

  val framesInputDir = config.getString("data-location.frames.input")
  val framesOutputDir = config.getString("data-location.frames.output")

  val moviesInputDir = config.getString("data-location.movies.input")
  val moviesOutputDir = config.getString("data-location.movies.output")
}
