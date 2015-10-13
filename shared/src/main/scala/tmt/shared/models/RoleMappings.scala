package tmt.shared.models

case class RoleMappings(mappings: Map[String, Seq[String]]) {
  def getServers(role: String) = mappings.getOrElse(role, Seq.empty)
  def roles = mappings.keySet.toSeq.sorted
  def pruneBy(onlineRoles: Set[String]) = RoleMappings {
    mappings.collect { case (role, serverNames) if onlineRoles.contains(role) =>
      role -> serverNames.filter(onlineRoles)
    }
  }
  def roleOf(serverName: String) = {
    mappings.find { mapping =>
      val servers = mapping._2
      servers.contains(serverName)
    }.map(_._1)
  }
}
