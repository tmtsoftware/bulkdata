package top.protocols

import akka.stream.scaladsl._
import akka.stream.{Materializer, OverflowStrategy}

object SourceFactory {
  def actorRef[T](bufferSize: Int, overflowStrategy: OverflowStrategy)(implicit materializer: Materializer) = {
    val (actorRef, publisher) = Source.actorRef[T](bufferSize, overflowStrategy).toMat(Sink.publisher)(Keep.both).run()
    (Source(publisher), actorRef)
  }
}
