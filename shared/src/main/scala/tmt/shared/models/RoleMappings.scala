package tmt.shared.models

case class RoleMappings(mappings: Map[Role, Seq[String]]) {
  def getServers(role: Role) = mappings.getOrElse(role, Seq.empty)
  def roles = mappings.keySet.toSeq.sortBy(_.entryName)
  def pruneBy(onlineRoles: Set[String]) = RoleMappings {
    mappings.collect { case (role, serverNames) if onlineRoles.contains(role.entryName) =>
      role -> serverNames.filter(onlineRoles)
    }
  }
}
