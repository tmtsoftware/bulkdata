package tmt.shared.models

import tmt.shared.Topics

case class NodeSet(nodes: Seq[Node]) {
  def compatibleConsumers(producer: String) = nodes.collectFirst {
    case Node(producerRole, `producer`, _, _) => nodes.collect {
      case Node(role, name, _, _) if producerRole.output == role.input && name != producer => name
    }
  }.toSeq.flatten

  def getRole(name: String) = nodes.collectFirst {
    case Node(role, `name`, _, _) => role
  }.getOrElse(Role.Empty)

  def getNames(role: Role) = nodes.collect {
    case Node(`role`, name, _, _) => name
  }

  def getNames(outputType: ItemType) = nodes.collect {
    case Node(role, name, _, _) if role.output == outputType => name 
  }

  def getWsUrl(name: String) = nodes.collectFirst {
    case n@Node(_, `name`, externalIp, httpPort) => s"ws://$externalIp:$httpPort/$name"  
  }

  def getScienceImageUrl(name: String) = nodes.collectFirst {
    case n@Node(_, `name`, externalIp, httpPort) => s"http://$externalIp:$httpPort/${Topics.ScienceImages}"
  }

  def producers = nodes.collect {
    case Node(role, server, _, _) if role.output.nonEmpty => server
  }

  def pruneBy(onlineRoles: Set[String]) = NodeSet(nodes.filter(_.isOnline(onlineRoles)))
}

object NodeSet {
  def empty = NodeSet(Seq.empty)
}

case class Node(role: Role, name: String, externalIp: String, httpPort: Int) {
  def isOnline(onlineRoles: Set[String]) = onlineRoles(role.entryName) && onlineRoles(name)
}

case class NodeS(role: String, name: String, externalIp: String, httpPort: Int) {
  def node = Node(Role.withName(role), name, externalIp, httpPort)
}

object NodeS {
  def fromNode(node: Node) = NodeS(node.role.entryName, node.name, node.externalIp, node.httpPort)
}
