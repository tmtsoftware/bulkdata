package top.common

import akka.actor.Status.{Failure, Success}
import akka.stream.{Materializer, OverflowStrategy}

trait Listener[T] {
  def onEvent(event: T): Unit
  def onError(ex: Throwable): Unit
  def onComplete(): Unit
}

class StreamListener[T](implicit mat: Materializer) extends Listener[T] {
  val (source, actorRef) = SourceFactory.pair[T](1000, OverflowStrategy.dropHead)
  override def onEvent(event: T) = actorRef ! event
  override def onError(ex: Throwable) = actorRef ! Failure(ex)
  override def onComplete() = actorRef ! Success("done")
}
