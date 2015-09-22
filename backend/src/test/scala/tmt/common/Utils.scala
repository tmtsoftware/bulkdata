package tmt.common

import scala.concurrent.duration.{Duration, DurationInt}
import scala.concurrent.{Await, Future}

object Utils {
  def await[T](f: Future[T], duration: Duration = 30.seconds) = Await.result(f, duration)
}
