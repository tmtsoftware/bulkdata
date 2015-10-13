package tmt.media

import akka.stream.scaladsl.Source
import org.scalatest.{MustMatchers, FunSuite}
import tmt.app.Assembly

import scala.concurrent.duration.DurationInt

class DDTest extends FunSuite with MustMatchers {

  val assembly = new Assembly("test")

  test("demo") {
    val duration = 2.millis
    val source = Source(duration, duration, ())
    val dd = source.groupedWithin(10000000, 1.second).map(_.size)

    import assembly.actorConfigs._

    dd.runForeach(println)

    Thread.sleep(10000)
  }

}
