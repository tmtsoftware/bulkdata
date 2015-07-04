package tmt.dsl

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import tmt.common.{Server, Config}

class DslServer(val interface: String, val port: Int) {
  implicit val system = ActorSystem("TMT")
  implicit val mat    = ActorMaterializer()

  import system.dispatcher

  val imageService = new ImageService

  val server = new Server(
    interface,
    port,
    new Pipeline(imageService, new AppRoute(new BoxService, imageService).route).connectionFlow
  )
}

object DslServer extends DslServer(Config.interface, Config.port) with App {
  server.run()
}
