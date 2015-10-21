package tmt.common

import akka.cluster.ddata.{LWWMapKey, ORMultiMapKey}
import tmt.shared.Topics
import tmt.shared.models.{RoleMappingS, RoleMapping}

object Keys {
  val Nodes = LWWMapKey[RoleMappingS](Topics.Nodes)
}
