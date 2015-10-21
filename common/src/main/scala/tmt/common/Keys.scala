package tmt.common

import akka.cluster.ddata.{LWWMapKey, ORMultiMapKey}
import tmt.shared.Topics
import tmt.shared.models.RoleMapping

object Keys {
  val Nodes = LWWMapKey[RoleMapping](Topics.Nodes)
}
