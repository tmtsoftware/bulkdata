package tmt.wavefront

import tmt.common.{Server, AppSettings, ActorConfigs}

class ServerFactory(routeInstances: RouteInstances, actorConfigs: ActorConfigs, appSettings: AppSettings) {
  import actorConfigs._
  def make() = {
    val route = routeInstances.find(appSettings.topology.role)
    val address = appSettings.topology.binding
    new Server(address, route, actorConfigs)
  }
}
