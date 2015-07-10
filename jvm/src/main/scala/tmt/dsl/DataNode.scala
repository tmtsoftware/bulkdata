package tmt.dsl

import tmt.common.{ActorConfigs, Config, Server}

class DataNode(interface: String, port: Int) {
  val actorConfigs = new ActorConfigs("TMT")
  import actorConfigs._

  val imageService = new MediaService
  val mediaRoute   = new MediaRoute(imageService)
  val server       = new Server(interface, port, mediaRoute.route)
}

object DataNode extends DataNode(Config.interface, Config.port) with App {
  server.run()
}
