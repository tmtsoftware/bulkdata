package tmt.wavefront

import tmt.common.{ActorConfigs, AppSettings, Server}
import tmt.media.server.MediaRoute

class ServerFactory(
  routeInstances: RouteInstances,
  actorConfigs: ActorConfigs,
  appSettings: AppSettings,
  mediaRoute: MediaRoute
) {

  import actorConfigs._

  def make() = appSettings.binding.role match {
    case "frontend" => new Server(appSettings.binding.httpAddress, mediaRoute.route, actorConfigs)
    case role       => new Server(appSettings.binding.httpAddress, routeInstances.find(Role.withName(role)), actorConfigs)
  }
}
