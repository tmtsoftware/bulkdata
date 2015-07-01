package top.common

import akka.actor.Status.{Failure, Success}
import akka.stream.{Materializer, OverflowStrategy}
import top.camera.Listener

class StreamListener[T](implicit mat: Materializer) extends Listener[T] {
  val (source, actorRef) = SourceFactory.pair[T](1000, OverflowStrategy.dropHead)
  override def onEvent(event: T) = actorRef ! event
  override def onError(ex: Throwable) = actorRef ! Failure(ex)
  override def onComplete() = actorRef ! Success("done")
}
