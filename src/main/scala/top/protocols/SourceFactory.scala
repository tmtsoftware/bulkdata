package top.protocols

import akka.stream.scaladsl._
import akka.stream.{FlowMaterializer, OverflowStrategy}

object SourceFactory {
  def actorRef[T](bufferSize: Int, overflowStrategy: OverflowStrategy)(implicit materializer: FlowMaterializer) = {
    val (actorRef, publisher) = Source.actorRef[T](bufferSize, overflowStrategy).toMat(Sink.publisher)(Keep.both).run()
    (Source(publisher), actorRef)
  }
}
