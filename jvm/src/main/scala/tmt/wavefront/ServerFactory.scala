package tmt.wavefront

import tmt.common.{MediaServerFactory, Server, AppSettings, ActorConfigs}

class ServerFactory(
  routeInstances: RouteInstances,
  actorConfigs: ActorConfigs,
  appSettings: AppSettings,
  mediaServerFactory: MediaServerFactory
) {
  import actorConfigs._

  def make() = appSettings.topology.role match {
    case "media" => mediaServerFactory.make()
    case role    => new Server(appSettings.topology.binding, routeInstances.find(role), actorConfigs)
  }
}
