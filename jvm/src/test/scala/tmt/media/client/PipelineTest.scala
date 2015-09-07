package tmt.media.client

import org.scalatest.{BeforeAndAfterAll, FunSuite, MustMatchers}
import tmt.common.Utils._
import tmt.media.MediaAssembly
import tmt.wavefront.Roles

class PipelineTest extends FunSuite with MustMatchers with BeforeAndAfterAll {

  val roles = Seq(
    Roles.ImageSource,
    Roles.ImageCopy,
    Roles.ImageFilter,
    Roles.MetricsCumulative,
    Roles.MetricsPerSec
  )

  val assemblies = roles.map(new MediaAssembly(_))
  val servers = assemblies.map(_.serverFactory.make())
  val bindings = servers.map(s => await(s.run()))

  test("run") {
    Thread.sleep(50000)
  }

  override protected def afterAll() = {
    bindings.foreach(b => await(b.unbind()))
    assemblies.foreach(_.system.shutdown())
  }
}
