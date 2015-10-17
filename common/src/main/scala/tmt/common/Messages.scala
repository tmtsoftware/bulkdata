package tmt.common

import tmt.shared.models.Connection

import scala.concurrent.duration.FiniteDuration

object Messages {
  case class UpdateDelay(serverName: String, value: FiniteDuration)
  case class Subscribe(connection: Connection)
  case class Unsubscribe(connection: Connection)
}
