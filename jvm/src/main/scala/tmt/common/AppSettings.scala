package tmt.common

import java.net.InetSocketAddress

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri
import collection.JavaConverters._

class AppSettings(actorConfigs: ActorConfigs) {

  import actorConfigs._

  val fileIoDispatcher = system.dispatchers.lookup("akka.stream.default-file-io-dispatcher")

  val config = system.settings.config
  val superPool = Http().superPool[Uri]()

  val framesInputDir = config.getString("data-location.frames.input")
  val framesOutputDir = config.getString("data-location.frames.output")

  val moviesInputDir = config.getString("data-location.movies.input")
  val moviesOutputDir = config.getString("data-location.movies.output")

  object topology {
    val role = config.getString("topology.role")
    val imageSources = {
      val imageSourceObjects = config.getConfigList("topology.image-sources").asScala
      imageSourceObjects.map(obj => new InetSocketAddress(obj.getString("interface"), obj.getInt("port")))
    }
    val binding = new InetSocketAddress(config.getString("topology.interface"), config.getInt("topology.port"))
  }
  
}
