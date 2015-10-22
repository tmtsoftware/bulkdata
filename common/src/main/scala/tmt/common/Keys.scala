package tmt.common

import akka.cluster.ddata.{LWWMapKey, ORMultiMapKey}
import tmt.shared.Topics
import tmt.shared.models.{NodeS$, Node}

object Keys {
  val Nodes = LWWMapKey[NodeS](Topics.Nodes)
}
