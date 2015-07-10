package tmt.dsl

import java.net.InetSocketAddress

import tmt.common.{ActorConfigs, Config, Server}

class DataNode(address: InetSocketAddress) {
  val actorConfigs = new ActorConfigs("TMT")
  import actorConfigs._

  val imageService = new MediaService
  val mediaRoute   = new MediaRoute(imageService)
  val server       = new Server(address, mediaRoute.route)
}

object DataNode extends DataNode(new InetSocketAddress(Config.interface, Config.port)) with App {
  server.run()
}
