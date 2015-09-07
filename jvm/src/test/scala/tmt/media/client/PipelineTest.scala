package tmt.media.client

import org.scalatest.{BeforeAndAfterAll, FunSuite, MustMatchers}
import tmt.common.Utils._
import tmt.media.MediaAssembly

class PipelineTest extends FunSuite with MustMatchers with BeforeAndAfterAll {

  val imageSourceAssembly = new MediaAssembly("ImageSource", "application.conf")
  val imageCopyAssembly = new MediaAssembly("ImageCopy", "image-copy.conf")
  val imageFilterAssembly = new MediaAssembly("ImageFilter", "image-filter.conf")
  val imageCumulativeAssembly = new MediaAssembly("CumulativeMetrics", "metrics-cumulative.conf")
  val imagePerSecAssembly = new MediaAssembly("PerSecMetrics", "metrics-per-sec.conf")

  val imageSourceServer = imageSourceAssembly.serverFactory.make()
  val imageCopyServer = imageCopyAssembly.serverFactory.make()
  val imageFilterServer = imageFilterAssembly.serverFactory.make()
  val imageCumulativeServer = imageCumulativeAssembly.serverFactory.make()
  val imagePerSecServer = imagePerSecAssembly.serverFactory.make()

  val imageSourceBinding = await(imageSourceServer.run())
  val imageCopyBinding = await(imageCopyServer.run())
  val imageFilterBinding = await(imageFilterServer.run())
  val imageCumulativeBinding = await(imageCumulativeServer.run())
  val imagePerSecBinding = await(imagePerSecServer.run())

  test("run") {
    Thread.sleep(50000)
  }

  override protected def afterAll() = {
    await(imageSourceBinding.unbind())
    await(imageCopyBinding.unbind())
    await(imageFilterBinding.unbind())
    await(imageCumulativeBinding.unbind())
    await(imagePerSecBinding.unbind())

    imageSourceAssembly.system.shutdown()
    imageCopyAssembly.system.shutdown()
    imageFilterAssembly.system.shutdown()
    imageCumulativeAssembly.system.shutdown()
    imagePerSecAssembly.system.shutdown()
  }
}
