package tmt.media.client

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.scaladsl.{FlattenStrategy, Sink, Source}
import org.scalatest.{BeforeAndAfterAll, FunSuite, MustMatchers}
import tmt.common.Utils._
import tmt.library.InetSocketAddressExtensions.RichInetSocketAddress
import tmt.media.MediaAssembly

import scala.concurrent.duration.DurationInt

class OneToManyTransferTest extends FunSuite with MustMatchers with BeforeAndAfterAll {

  val testAssembly = new MediaAssembly("application")
  import testAssembly.actorConfigs._

  val sourceAssembly = new MediaAssembly("source")
  val destination1Assembly = new MediaAssembly("destination1")
  val destination2Assembly = new MediaAssembly("destination2")

  val sourceServer = sourceAssembly.mediaServerFactory.make()
  val destination1Server = destination1Assembly.mediaServerFactory.make()
  val destination2Server = destination2Assembly.mediaServerFactory.make()

  val source = sourceAssembly.appSettings.topology.binding
  val destination1 = destination1Assembly.appSettings.topology.binding
  val destination2 = destination2Assembly.appSettings.topology.binding

  val oneToManyTransfer = testAssembly.oneToManyTransferFactory.make(source, Seq(destination1, destination2))

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
