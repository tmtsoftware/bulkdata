package tmt.shared.models

case class Topology(nodes: Seq[Node]) {
  private val indexByRole = nodes.groupBy(_.role).mapValues(_.map(_.name))

  val inputTypeIndex = ItemTypeIndex(nodes, _.input)
  val outputTypeIndex = ItemTypeIndex(nodes, _.output)
  val nodeNameIndex = NodeNameIndex(nodes)

  def compatibleConsumers(producer: String) =  {
    val outputType = nodeNameIndex.getRole(producer).output
    inputTypeIndex.getNodeNames(outputType).filterNot(_ == producer) //filter itself to avoid cycles
  }

  def getNodeNames(role: Role) = indexByRole.getOrElse(role, Seq.empty)

  def producers = nodes.collect {
    case Node(role, server, _, _) if role.output.nonEmpty => server
  }

  def pruneBy(onlineRoles: Set[String]) = Topology(nodes.filter(_.isOnline(onlineRoles)))
}

object Topology {
  def empty = Topology(Seq.empty)
}

case class NodeNameIndex(nodes: Seq[Node]) {
  private val indexByName = nodes.map(x => x.name -> x).toMap
  def getRole(name: String) = indexByName.get(name).map(_.role).getOrElse(Role.Empty)
  def getHost(name: String) = indexByName.get(name).map(_.websocketUrl)
}

case class ItemTypeIndex(nodes: Seq[Node], f: Role => ItemType) {
  private val indexByItemType = nodes.groupBy(x => f(x.role)).mapValues(_.map(_.name))
  def getNodeNames(itemType: ItemType) = indexByItemType.getOrElse(itemType, Seq.empty)
}

case class Node(role: Role, name: String, externalIp: String, httpPort: Int) {
  def websocketUrl = s"ws://$externalIp:$httpPort/$name"
  def isOnline(onlineRoles: Set[String]) = onlineRoles(role.entryName) && onlineRoles(name)
}

case class NodeS(role: String, name: String, externalIp: String, httpPort: Int) {
  def node = Node(Role.withName(role), name, externalIp, httpPort)
}

object NodeS {
  def fromNode(node: Node) = 
    NodeS(node.role.entryName, node.name, node.externalIp, node.httpPort)
}
