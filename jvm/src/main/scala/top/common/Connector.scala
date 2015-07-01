package top.common

import akka.stream.scaladsl.{Keep, Sink, Source}
import akka.stream.{Materializer, OverflowStrategy}

object Connector {
  def coupling[T](bufferSize: Int, overflowStrategy: OverflowStrategy)(implicit materializer: Materializer) = {
    val (actorRef, publisher) = Source.actorRef[T](bufferSize, overflowStrategy).toMat(Sink.publisher)(Keep.both).run()
    (actorRef, Source(publisher))
  }
}
