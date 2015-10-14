package tmt.shared.models

case class RoleIndex(mappings: Seq[RoleMapping]) {
  private val index = mappings.groupBy(_.role).mapValues(_.map(_.server))

  val outputTypeIndex = ItemTypeIndex.from(this, _.maybeOutput)
  val inputTypeIndex = ItemTypeIndex.from(this, _.maybeInput)
  val itemTypes = (outputTypeIndex.itemTypes ++ inputTypeIndex.itemTypes).distinct.sortBy(_.entryName)

  def getServers(role: Role) = index.getOrElse(role, Seq.empty)
  def pruneBy(onlineRoles: Set[String]) = RoleIndex(mappings.filter(_.isOnline(onlineRoles)))
}

case class ItemTypeIndex(entries: Seq[ItemTypeMapping]) {
  private val index = entries.groupBy(_.itemType).mapValues(_.map(_.server))
  def itemTypes = index.keySet.toSeq.sortBy(_.entryName)
  def getServers(itemType: ItemType) = index.getOrElse(itemType, Seq.empty)
}

object ItemTypeIndex {
  def from(roleIndex: RoleIndex, f: Role => Option[ItemType]) = ItemTypeIndex {
    for {
      RoleMapping(role, server) <- roleIndex.mappings
      input <- f(role).toSeq
    } yield ItemTypeMapping(input, server)
  }
}

case class RoleMapping(role: Role, server: String) {
  def isOnline(onlineRoles: Set[String]) = onlineRoles(role.entryName) && onlineRoles(server)
}

case class ItemTypeMapping(itemType: ItemType, server: String)
