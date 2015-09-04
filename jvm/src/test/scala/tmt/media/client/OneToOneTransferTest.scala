package tmt.media.client

import java.net.InetSocketAddress

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.scaladsl.{FlattenStrategy, Sink, Source}
import org.scalatest.{BeforeAndAfterAll, FunSuite, MustMatchers}
import tmt.common.Utils._
import tmt.library.InetSocketAddressExtensions.RichInetSocketAddress
import tmt.media.MediaAssembly

import scala.concurrent.duration.DurationInt

class OneToOneTransferTest extends FunSuite with MustMatchers with BeforeAndAfterAll {

  val testAssembly = new MediaAssembly("Test")
  import testAssembly.actorConfigs._

  val source = new InetSocketAddress("localhost", 7001)
  val destination = new InetSocketAddress("localhost", 8001)

  val sourceAssembly = new MediaAssembly("Source")
  val destinationAssembly = new MediaAssembly("Destination")

  val sourceServer = sourceAssembly.serverFactory.make(source)
  val destinationServer = destinationAssembly.serverFactory.make(destination)

  val oneToOneTransfer = testAssembly.oneToOneTransferFactory.make(source, destination)
  val simpleTransfer = testAssembly.simpleTransferFactory.make(source, destination)

  val sourceBinding = await(sourceServer.run())
  val destinationBinding = await(destinationServer.run())

  test("frame-bytes") {
    val transferResponse = await(simpleTransfer.singleTransfer("/images/bytes"))
    verifyTransfer(transferResponse)
  }

  test("frame-objects") {
    val transferResponse = await(simpleTransfer.singleTransfer("/images/objects"))
    verifyTransfer(transferResponse)
  }

  test("blob-basic") {
    val result = movieNames
      .mapAsync(1) { name => simpleTransfer.singleTransfer(s"/movies/$name") }
      .map(verifyTransfer)
      .runWith(Sink.ignore)

    await(result, 3.minute)
  }
  
  test("blob-pipelined") {
    val result = movieNames
      .map(name => s"/movies/$name")
      .via(oneToOneTransfer.flow)
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
    await(destinationBinding.unbind())
    sourceAssembly.system.shutdown()
    destinationAssembly.system.shutdown()
    system.shutdown()
  }
}
