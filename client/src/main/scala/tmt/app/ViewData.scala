package tmt.app

import rx._
import tmt.shared.models._

class ViewData(val nodeSet: Rx[NodeSet], val connectionSet: Rx[ConnectionSet]) {
  val producers = Rx(nodeSet().producers)
  def consumersOf(topicName: Rx[String]) = Rx(nodeSet().compatibleConsumers(topicName()))

  val imageServers = Rx(nodeSet().getNames(ItemType.Image))
  val frequencyServers = Rx(nodeSet().getNames(ItemType.Frequency))

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

  val sourceServers = Rx(nodeSet().getNames(Role.Source))
}
