package tmt.common.models

import scala.concurrent.duration.FiniteDuration

object Throttle {
  case class UpdateDelay(value: FiniteDuration)
}
