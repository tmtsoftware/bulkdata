package tmt.shared.models

case class RoleIndex(mappings: Seq[RoleMapping]) {
  private val index = mappings.groupBy(_.role).mapValues(_.map(_.server))

  val inputTypeIndex = ItemTypeIndex(mappings, _.input)
  val outputTypeIndex = ItemTypeIndex(mappings, _.output)
  val serverNameIndex = ServerNameIndex(mappings)

  def compatibleConsumers(producer: String) =  {
    val outputType = serverNameIndex.getRole(producer).output
    inputTypeIndex.getServers(outputType).filterNot(_ == producer) //filter itself to avoid cycles
  }

  def getServers(role: Role) = index.getOrElse(role, Seq.empty)

  def producers = mappings.collect {
    case RoleMapping(role, server) if role.output.nonEmpty => server
  }

  def pruneBy(onlineRoles: Set[String]) = RoleIndex(mappings.filter(_.isOnline(onlineRoles)))
}

object RoleIndex {
  def empty = RoleIndex(Seq.empty)
}

case class ServerNameIndex(mappings: Seq[RoleMapping]) {
  private val index = mappings.map(x => x.server -> x.role).toMap
  def getRole(server: String) = index.getOrElse(server, Role.Invalid)
}

case class ItemTypeIndex(mappings: Seq[RoleMapping], f: Role => ItemType) {
  private val index = mappings.groupBy(x => f(x.role)).mapValues(_.map(_.server))
  def getServers(itemType: ItemType) = index.getOrElse(itemType, Seq.empty)
}

case class RoleMapping(role: Role, server: String) {
  def isOnline(onlineRoles: Set[String]) = onlineRoles(role.entryName) && onlineRoles(server)
}
