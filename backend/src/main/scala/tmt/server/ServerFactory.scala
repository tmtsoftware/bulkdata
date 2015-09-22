package tmt.server

import tmt.app.{ActorConfigs, AppSettings}

class ServerFactory(
  routeInstances: RouteInstances,
  actorConfigs: ActorConfigs,
  appSettings: AppSettings
) {

  import actorConfigs._

  def make() = {
    val role = Role.withName(appSettings.binding.role)
    new Server(appSettings.binding.httpAddress, routeInstances.find(role), actorConfigs)
  }
}
