package tmt.shared.models

case class RoleMappings(mappings: Map[String, Seq[String]]) {
  def getServers(role: String) = mappings.getOrElse(role, Seq.empty)
  def roles = mappings.keySet.toSeq.sorted
}
