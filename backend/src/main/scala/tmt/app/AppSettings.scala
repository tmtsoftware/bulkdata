package tmt.app

import java.net.InetSocketAddress

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri
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

  val env = config.getString("env")

  object binding {
    val name = config.getString("binding.name")
    val role = config.getString("binding.role")
    val hostname = config.getString("binding.hostname")
    val httpPort = config.getInt("binding.http-port")
    val httpAddress = new InetSocketAddress(hostname, httpPort)
  }
}
