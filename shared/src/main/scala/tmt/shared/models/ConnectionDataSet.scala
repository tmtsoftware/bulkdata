package tmt.shared.models

case class ConnectionDataSet(connections: Map[String, Set[String]]) {
  def flatConnections = {
    for {
      (serverName, topics) <- connections.toSeq
      topic <- topics
    } yield (serverName, topic)
  }.sorted
}
