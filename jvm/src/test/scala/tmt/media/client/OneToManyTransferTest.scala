package tmt.media.client

import java.net.InetSocketAddress

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.scaladsl.{FlattenStrategy, Sink, Source}
import org.scalatest.{BeforeAndAfterAll, FunSuite, MustMatchers}
import tmt.common.AppSettings
import tmt.common.Utils._
import tmt.library.InetSocketAddressExtensions.RichInetSocketAddress
import tmt.media.MediaAssembly

import scala.concurrent.duration.DurationInt

class OneToManyTransferTest extends FunSuite with MustMatchers with BeforeAndAfterAll {

  val testAssembly = new MediaAssembly("Test")
  import testAssembly.actorConfigs._

  val sourceAssembly = new MediaAssembly("Source")
  val destination1Assembly = new MediaAssembly("Destination1")
  val destination2Assembly = new MediaAssembly("Destination2") {
    override lazy val appSettings = new AppSettings(actorConfigs) {
      override val framesOutputDir = "/usr/local/data/tmt/movies/output2"
    }
  }

  val source = new InetSocketAddress("localhost", 7001)
  val destination1 = new InetSocketAddress("localhost", 8001)
  val destination2 = new InetSocketAddress("localhost", 8002)

  val sourceServer = sourceAssembly.mediaServer(source)
  val destination1Server = destination1Assembly.mediaServer(destination1)

  val destination2Server = destination2Assembly.mediaServer(destination2)
  val oneToManyTransfer = testAssembly.oneToManyTransfer(source, Seq(destination1, destination2))

  val sourceBinding = await(sourceServer.run())
  val destination1Binding = await(destination1Server.run())
  val destination2Binding = await(destination2Server.run())

  test("blob-pipelined") {
    val result = movieNames
      .map(name => s"/movies/$name")
      .via(oneToManyTransfer.flow)
      .map(verifyTransfer)
      .runWith(Sink.ignore)

    await(result, 3.minute)
  }

  def verifyTransfer(response: HttpResponse) = {
    response.status mustEqual StatusCodes.OK
    response.entity.asInstanceOf[HttpEntity.Strict].data.utf8String mustEqual "copied"
  }

  def movieNames = {
    val listRequest = HttpRequest(uri = source.absoluteUri("/movies/list"))

    Source(Http().singleRequest(listRequest))
      .map(_.entity.dataBytes.map(_.utf8String))
      .flatten(FlattenStrategy.concat)
      .take(4)
  }

  override protected def afterAll() = {
    await(sourceBinding.unbind())
    await(destination1Binding.unbind())
    await(destination2Binding.unbind())
    sourceAssembly.system.shutdown()
    destination1Assembly.system.shutdown()
    destination2Assembly.system.shutdown()
    testAssembly.system.shutdown()
  }
}
