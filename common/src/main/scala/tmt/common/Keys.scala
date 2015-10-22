package tmt.common

import akka.cluster.ddata.LWWMapKey
import tmt.shared.Topics
import tmt.shared.models.NodeS

object Keys {
  val Nodes = LWWMapKey[NodeS](Topics.Nodes)
}
