package tmt.dsl

import java.net.InetSocketAddress

import tmt.common.{ActorConfigs, SharedConfigs, Server}

class DataNode(address: InetSocketAddress)(implicit val actorConfigs: ActorConfigs) {
  import actorConfigs._

  val imageService = new MediaService
  val mediaRoute   = new MediaRoute(imageService)
  val server       = new Server(address, mediaRoute.route)
}

object DataNode extends DataNode(new InetSocketAddress(SharedConfigs.interface, SharedConfigs.port))(ActorConfigs.from("data-node")) {
  server.run()
}
