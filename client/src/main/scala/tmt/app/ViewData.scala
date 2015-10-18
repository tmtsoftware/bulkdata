package tmt.app

import rx._
import tmt.shared.models._

class ViewData(val roleIndex: Rx[RoleIndex], val connectionSet: Rx[ConnectionSet], val hostMappings: Rx[HostMappings]) {
  val producers = Rx(roleIndex().producers)
  def consumersOf(topicName: Rx[String]) = Rx(roleIndex().compatibleConsumers(topicName()))

  val imageServers = Rx(roleIndex().outputTypeIndex.getServers(ItemType.Image))
  val frequencyServers = Rx(roleIndex().outputTypeIndex.getServers(ItemType.Frequency))

  val allServers = Rx(imageServers() ++ frequencyServers())

  val diffs = {
    val result = Var(Seq.empty[String])
    var current = allServers()
    Obs(allServers) {
      result() = allServers().diff(current)
      current = allServers()
    }
    result
  }

  val imageWsUrls = Rx(imageServers().map(hostMappings().getHost))
  val frequencyWsUrls = Rx(frequencyServers().map(hostMappings().getHost))

  val sourceServers = Rx(roleIndex().getServers(Role.Source))
}
