package tmt.shared.models

case class RoleIndex(mappings: Seq[RoleMapping]) {
  private val index = mappings.groupBy(_.role).mapValues(_.map(_.server))

  val inputTypeIndex = new ItemTypeIndex(mappings, _.maybeInput)
  val outputTypeIndex = new ItemTypeIndex(mappings, _.maybeOutput)
  val serverNameIndex = ServerNameIndex(mappings)

  val itemTypes = (outputTypeIndex.itemTypes ++ inputTypeIndex.itemTypes).distinct.sortBy(_.entryName)

  def compatibleConsumers(producer: String) =  {
    val outputType = serverNameIndex.getOutputType(producer)
    inputTypeIndex.getServers(outputType).filterNot(_ == producer) //filter itself to avoid cycles
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
  def getOutputType(server: String) = getRole(server).flatMap(_.maybeOutput).getOrElse(ItemType.Invalid)
}

class ItemTypeIndex(mappings: Seq[RoleMapping], f: Role => Option[ItemType]) {
  val entries = for {
    RoleMapping(role, server) <- mappings
    input <- f(role).toSeq
  } yield ItemTypeMapping(input, server)

  private val index = entries.groupBy(_.itemType).mapValues(_.map(_.server))
  def getServers(itemType: ItemType) = index.getOrElse(itemType, Seq.empty)
  def itemTypes = index.keySet.toSeq
}

case class RoleMapping(role: Role, server: String) {
  def isOnline(onlineRoles: Set[String]) = onlineRoles(role.entryName) && onlineRoles(server)
}

case class ItemTypeMapping(itemType: ItemType, server: String)
