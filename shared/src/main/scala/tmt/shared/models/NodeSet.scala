package tmt.shared.models

case class NodeSet(nodes: Seq[Node]) {
  val pipelineNodes = nodes.filterNot(_.role == Role.ImageSource)

  def compatibleConsumers(producer: String) = pipelineNodes.collectFirst {
    case Node(producerRole, `producer`, _, _) => pipelineNodes.collect {
      case Node(role, name, _, _) if producerRole.output == role.input && name != producer => name
    }
  }.toSeq.flatten

  def getRole(name: String) = pipelineNodes.collectFirst {
    case Node(role, `name`, _, _) => role
  }.getOrElse(Role.Empty)

  def getNames(role: Role) = pipelineNodes.collect {
    case Node(`role`, name, _, _) => name
  }

  def getNames(outputType: ItemType) = pipelineNodes.collect {
    case Node(role, name, _, _) if role.output == outputType => name 
  }

  def getHost(name: String) = pipelineNodes.collectFirst {
    case n@Node(_, `name`, externalIp, httpPort) => s"ws://$externalIp:$httpPort/$name"  
  }

  def producers = pipelineNodes.collect {
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
