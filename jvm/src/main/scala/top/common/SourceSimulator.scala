package top.common

import akka.stream.scaladsl._
import akka.stream.{Materializer, OverflowStrategy}
import top.camera.Simulator

object SourceSimulator {
  def apply[T](producer: () => Iterator[T])(implicit materializer: Materializer) = {
    val simulator = new Simulator(producer())
    val listener = new StreamListener[T]
    simulator.subscribe(listener)
    listener.source
  }
}
