package tmt.app

import java.net.InetSocketAddress

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri
import tmt.shared.models.{RoleMapping, Role}
import scala.concurrent.duration.DurationLong
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

  val imageReadThrottle = config.getDuration("image-read-throttle").toMillis.millis

  val imageProcessingThreadPoolSize = config.getInt("image-processing.thread-pool-size")
  val imageProcessingParallelism = config.getInt("image-processing.parallelism")

  val env = config.getString("env")

  object binding {
    val name = config.getString("binding.name")
    val role = config.getString("binding.role")
    val hostname = config.getString("binding.hostname")
    val externalIp = config.getString("binding.externalIp")
    val httpAddress = new InetSocketAddress(hostname, 0)
    def roleMapping(httpPort: Int) = RoleMapping(Role.withName(role), name, externalIp, httpPort)
  }
}
