package tmt.common

import akka.cluster.ddata.ORMultiMapKey
import tmt.shared.Topics

object Keys {
  val Connections = ORMultiMapKey[String](Topics.Connections)
}
