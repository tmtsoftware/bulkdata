package tmt.media.client

import org.scalatest.{BeforeAndAfterAll, FunSuite, MustMatchers}
import tmt.wavefront.{Roles, RunningServer}

class PipelineTest extends FunSuite with MustMatchers with BeforeAndAfterAll {

  val roles = Seq(
    Roles.ImageSource,
    Roles.ImageCopy,
    Roles.ImageFilter,
    Roles.MetricsCumulative,
    Roles.MetricsPerSec
  )

  val runningServers = roles.map(new RunningServer(_))

  test("run") {
    Thread.sleep(30000)
  }

  override protected def afterAll() = {
    runningServers.foreach(_.stop())
  }
}
