package tmt.common

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

object Utils {
  def await[T](f: Future[T]) = Await.result(f, 30.seconds)
}
