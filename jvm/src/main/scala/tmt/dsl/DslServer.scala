package tmt.dsl

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import tmt.common.{Server, Config}

class DslServer(val interface: String, val port: Int) {
  implicit val system = ActorSystem("TMT")
  implicit val mat    = ActorMaterializer()
  implicit val ec     = system.dispatcher

  val imageService = new MediaService

  val server = new Server(
    interface,
    port,
    new MediaRoute(imageService).route
  )
}

object DslServer extends DslServer(Config.interface, Config.port) with App {
  server.run()
}
