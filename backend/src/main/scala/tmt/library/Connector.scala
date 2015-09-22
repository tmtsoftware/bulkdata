package tmt.library

import akka.stream.scaladsl.{Keep, Sink, Source}
import akka.stream.{Materializer, OverflowStrategy}
import org.reactivestreams.Publisher

object Connector {
  def coupling[T](sink: Sink[T, Publisher[T]])(implicit mat: Materializer) = {
    val (actorRef, publisher) = Source.actorRef[T](2, OverflowStrategy.dropHead).toMat(sink)(Keep.both).run()
    (actorRef, Source(publisher))
  }
}
