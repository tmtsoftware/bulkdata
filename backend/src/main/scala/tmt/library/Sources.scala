package tmt.library

import akka.stream.scaladsl.Source

import scala.concurrent.duration.FiniteDuration

object Sources {
  def ticks(duration: FiniteDuration) = Source(duration, duration, ())
  def interval(duration: FiniteDuration) = ticks(duration).scan(0)((acc, elm) => acc + 1).drop(1)
  def numbers = Source(() => Iterator.from(1))
}
