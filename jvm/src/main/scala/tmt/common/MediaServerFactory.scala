package tmt.common

import tmt.media.server.MediaRoute

class MediaServerFactory(actorConfigs: ActorConfigs, mediaRoute: MediaRoute, appSettings: AppSettings) {
  import actorConfigs._
  def make() = new Server(appSettings.topology.binding, mediaRoute.route, actorConfigs)
}
