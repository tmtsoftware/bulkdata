package tmt.common

import java.net.InetSocketAddress

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri
import collection.JavaConverters._
import scala.util.Try

class AppSettings(actorConfigs: ActorConfigs) {

  import actorConfigs._

  val fileIoDispatcher = system.dispatchers.lookup("akka.stream.default-file-io-dispatcher")

  val config = system.settings.config
  val superPool = Http().superPool[Uri]()

  val maxTransferFiles = Try(config.getInt("max-transfer-files")).getOrElse(Int.MaxValue)

  val framesInputDir = config.getString("data-location.frames.input")
  val framesOutputDir = config.getString("data-location.frames.output")

  val moviesInputDir = config.getString("data-location.movies.input")
  val moviesOutputDir = config.getString("data-location.movies.output")

  object topology {
    val role = config.getString("topology.role")
    val sources = {
      val sourceObjects = config.getConfigList("topology.sources").asScala
      sourceObjects.map(obj => new InetSocketAddress(obj.getString("interface"), obj.getInt("port")))
    }
    val binding = new InetSocketAddress(config.getString("topology.binding.interface"), config.getInt("topology.binding.port"))
  }
  
}
