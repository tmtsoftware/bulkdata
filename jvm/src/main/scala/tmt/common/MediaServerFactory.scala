package tmt.common

import java.net.InetSocketAddress

import tmt.media.server.MediaRoute

class MediaServerFactory(actorConfigs: ActorConfigs, mediaRoute: MediaRoute) {
  import actorConfigs._
  def make(address: InetSocketAddress) = new Server(address, mediaRoute.route, actorConfigs)
}
