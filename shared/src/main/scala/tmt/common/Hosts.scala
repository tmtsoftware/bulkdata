package tmt.common

object Hosts {

  val dev = Map(
    "frontend" -> "127.0.0.1",
    "image-source" -> "127.0.0.1",
    "metrics-agg" -> "127.0.0.1"
  )

  val prod = Map(
    "frontend" -> "52.21.60.251",
    "image-source" -> "52.2.10.118",
    "metrics-agg" -> "52.22.19.96"
  )


  def getHosts(env: String) = env match {
    case "dev"  => dev
    case "prod" => prod
  }

  def getHost(env: String, name: String) = getHosts(env)(name)
}
