package tmt.common

object Hosts {

  val dev = Map(
    "hosts.frontend" -> "127.0.0.1",
    "hosts.image-source" -> "127.0.0.1",
    "hosts.image-copy" -> "127.0.0.1",
    "hosts.image-filter" -> "127.0.0.1",
    "hosts.metrics-per-sec" -> "127.0.0.1",
    "hosts.metrics-agg" -> "127.0.0.1"
  )

  val prod = Map(
    "hosts.frontend" -> "172.31.7.68",
    "hosts.image-source" -> "172.31.0.94",
    "hosts.image-copy" -> "172.31.9.243",
    "hosts.image-filter" -> "172.31.11.200",
    "hosts.metrics-per-sec" -> "172.31.9.242",
    "hosts.metrics-agg" -> "172.31.11.97"
  )


  def getHosts(env: String) = env match {
    case "dev"  => dev
    case "prod" => prod
  }

  def getHost(env: String, name: String) = getHosts(env)(name)
}
