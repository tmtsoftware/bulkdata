package tmt.shared.models

case class RoleIndex(mappings: Seq[RoleMapping]) {
  private val index = mappings.groupBy(_.role).mapValues(_.map(_.server))

  val inputTypeIndex = ItemTypeIndex.from(mappings, _.maybeInput)
  val outputTypeIndex = ItemTypeIndex.from(mappings, _.maybeOutput)
  val serverNameIndex = ServerNameIndex(mappings)

  val itemTypes = (outputTypeIndex.itemTypes ++ inputTypeIndex.itemTypes).distinct.sortBy(_.entryName)

  def getMatchingServers(server: String) =  {
    val outputType = serverNameIndex.getOutputType(server)
    inputTypeIndex.getServers(outputType).filterNot(_ == server) //filter to avoid cycles
  }

  def getServers(role: Role) = index.getOrElse(role, Seq.empty)
  def producers = mappings.collect {
    case RoleMapping(role, server) if role.isProducer => server
  }
  def pruneBy(onlineRoles: Set[String]) = RoleIndex(mappings.filter(_.isOnline(onlineRoles)))
}

case class ServerNameIndex(mappings: Seq[RoleMapping]) {
  private val index = mappings.map(x => x.server -> x.role).toMap
  def getRole(server: String) = index.get(server)
  def getOutputType(server: String) = index.get(server).flatMap(_.maybeOutput).getOrElse(ItemType.Invalid)
}

case class ItemTypeIndex(entries: Seq[ItemTypeMapping]) {
  private val index = entries.groupBy(_.itemType).mapValues(_.map(_.server))
  def itemTypes = index.keySet.toSeq.sortBy(_.entryName)
  def getServers(itemType: ItemType) = index.getOrElse(itemType, Seq.empty)
}

object ItemTypeIndex {
  def from(mappings: Seq[RoleMapping], f: Role => Option[ItemType]) = ItemTypeIndex {
    for {
      RoleMapping(role, server) <- mappings
      input <- f(role).toSeq
    } yield ItemTypeMapping(input, server)
  }
}

case class RoleMapping(role: Role, server: String) {
  def isOnline(onlineRoles: Set[String]) = onlineRoles(role.entryName) && onlineRoles(server)
}

case class ItemTypeMapping(itemType: ItemType, server: String)
