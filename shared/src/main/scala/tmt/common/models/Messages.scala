package tmt.common.models

import scala.concurrent.duration.FiniteDuration

object Messages {
  case class UpdateDelay(serverName: String, value: FiniteDuration)
  case class Subscribe(serverName: String, topic: String)
  case class Unsubscribe(serverName: String, topic: String)
}
