package tmt.dsl

import tmt.common.{ActorConfigs, Config, Server}

class DslServer(val interface: String, val port: Int) {
  val actorConfigs = new ActorConfigs("TMT")
  import actorConfigs._

  val imageService = new MediaService
  val mediaRoute   = new MediaRoute(imageService)
  val server       = new Server(interface, port, mediaRoute.route)
}

object DslServer extends DslServer(Config.interface, Config.port) with App {
  server.run()
}
