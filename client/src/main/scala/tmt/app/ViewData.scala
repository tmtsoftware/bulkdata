package tmt.app

import rx._
import tmt.shared.models._

class ViewData(val roleIndex: Rx[Topology], val connectionSet: Rx[ConnectionSet]) {
  val producers = Rx(roleIndex().producers)
  def consumersOf(topicName: Rx[String]) = Rx(roleIndex().compatibleConsumers(topicName()))

  val imageServers = Rx(roleIndex().outputTypeIndex.getNodeNames(ItemType.Image))
  val frequencyServers = Rx(roleIndex().outputTypeIndex.getNodeNames(ItemType.Frequency))

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

  val imageWsUrls = Rx(imageServers().map(roleIndex().nodeNameIndex.getHost))
  val frequencyWsUrls = Rx(frequencyServers().map(roleIndex().nodeNameIndex.getHost))

  val sourceServers = Rx(roleIndex().getNodeNames(Role.Source))
}
