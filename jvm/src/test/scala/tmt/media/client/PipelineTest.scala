package tmt.media.client

import org.scalatest.{BeforeAndAfterAll, FunSuite, MustMatchers}
import tmt.common.Sources
import tmt.common.Utils._
import tmt.library.SourceExtensions.RichSource
import tmt.media.MediaAssembly

import scala.concurrent.duration._

class PipelineTest extends FunSuite with MustMatchers with BeforeAndAfterAll {

  val testAssembly = new MediaAssembly("Test")
  val imageSourceAssembly = new MediaAssembly("ImageSource")
  val imageCopyAssembly = new MediaAssembly("ImageCopy", "image-copy.conf")

  import testAssembly.actorConfigs._

  val imageSourceServer = imageSourceAssembly.serverFactory.make()
  val imageCopyServer = imageCopyAssembly.serverFactory.make()

  val imageSourceBinding = await(imageSourceServer.run())
  val imageCopyBinding = await(imageCopyServer.run())

  test("run") {
    Thread.sleep(10000)
  }

  override protected def afterAll() = {
    await(imageSourceBinding.unbind())
    await(imageCopyBinding.unbind())
    imageSourceAssembly.system.shutdown()
    imageCopyAssembly.system.shutdown()
    system.shutdown()
  }
}
