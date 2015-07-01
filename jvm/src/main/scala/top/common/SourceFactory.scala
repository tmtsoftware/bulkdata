package top.common

import akka.stream.scaladsl._
import akka.stream.{Materializer, OverflowStrategy}
import top.camera.Simulator

object SourceFactory {

  def pair[T](bufferSize: Int, overflowStrategy: OverflowStrategy)(implicit materializer: Materializer) = {
    val (actorRef, publisher) = Source.actorRef[T](bufferSize, overflowStrategy).toMat(Sink.publisher)(Keep.both).run()
    (Source(publisher), actorRef)
  }

  def from[T](producer: Iterator[T])(implicit materializer: Materializer) = {
    val simulator = new Simulator(producer)
    val listener = new StreamListener[T]
    simulator.subscribe(listener)
    listener.source
  }
}
