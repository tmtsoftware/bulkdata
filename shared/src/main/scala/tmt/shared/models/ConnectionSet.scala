package tmt.shared.models

case class ConnectionSet(connections: Set[Connection]) {
  lazy val index = connections.groupBy(_.server).mapValues(_.map(_.topic))

  def getTopics(serverName: String) = index.getOrElse(serverName, Set.empty)

  def add(connection: Connection) = ConnectionSet(connections + connection)
  def remove(connection: Connection) = ConnectionSet(connections - connection)

  def pruneBy(onlineRoles: Set[String]) = ConnectionSet(
    connections.filter(_.isOnline(onlineRoles))
  )
}

object ConnectionSet {
  def empty = ConnectionSet(Set.empty)
}

case class Connection(server: String, topic: String) {
  def isOnline(onlineRoles: Set[String]) =
    onlineRoles.contains(server) && onlineRoles.contains(topic)
}
