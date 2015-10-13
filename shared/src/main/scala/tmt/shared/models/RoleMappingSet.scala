package tmt.shared.models

case class RoleMappingSet(roleMappings: Map[Role, Seq[String]]) {
  
  val outputTypeMappings = for {
    (role, servers) <- roleMappings.toSeq
    input <- role.maybeOutput.toSeq
    server <- servers
  } yield ItemTypeMapping(input, server)
  
  val indexByOutputType = outputTypeMappings.groupBy(_.itemType).mapValues(_.map(_.server))
  
  def getServersByOutputType(itemType: ItemType) = indexByOutputType.getOrElse(itemType, Seq.empty)
  
  def getServersByRole(role: Role) = roleMappings.getOrElse(role, Seq.empty)
  def roles = roleMappings.keySet.toSeq.sortBy(_.entryName)
  def pruneBy(onlineRoles: Set[String]) = RoleMappingSet {
    roleMappings.collect { case (role, serverNames) if onlineRoles.contains(role.entryName) =>
      role -> serverNames.filter(onlineRoles)
    }
  }
}

case class RoleMapping(role: Role, server: String)

case class ItemTypeMapping(itemType: ItemType, server: String)
