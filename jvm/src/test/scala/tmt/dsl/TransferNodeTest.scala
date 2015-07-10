package tmt.dsl

import java.net.InetSocketAddress

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.scaladsl.{FlattenStrategy, Sink, Source}
import org.scalatest.{BeforeAndAfterAll, FunSuite, MustMatchers}
import tmt.common.ActorConfigs
import tmt.common.Utils._

import scala.concurrent.duration.DurationInt

class TransferNodeTest extends FunSuite with MustMatchers with BeforeAndAfterAll {

  val testConfigs = new ActorConfigs("Test")
  import testConfigs._

  val source = new InetSocketAddress("localhost", 7001)
  val destination = new InetSocketAddress("localhost", 8001)

  val sourceServer = new DataNode(source)
  val destinationServer = new DataNode(destination)
  val transferNode = new TransferNode(source, destination)

  val sourceBinding = await(sourceServer.server.run())
  val destinationBinding = await(destinationServer.server.run())

  test("frame-bytes") {
    val transferResponse = await(transferNode.singleTransfer("/images/bytes"))
    verifyTransfer(transferResponse)
  }

  test("frame-objects") {
    val transferResponse = await(transferNode.singleTransfer("/images/objects"))
    verifyTransfer(transferResponse)
  }

  test("blob-basic") {
    val result = movieNames
      .mapAsync(1) { name => transferNode.singleTransfer(s"/movies/$name") }
      .map(verifyTransfer)
      .runWith(Sink.ignore)

    await(result, 3.minute)
  }
  
  test("blob-pipelined") {
    val result = movieNames
      .map(name => s"/movies/$name")
      .via(transferNode.pipelinedTransfer)
      .map(verifyTransfer)
      .runWith(Sink.ignore)

    await(result, 3.minute)
  }

  def verifyTransfer(response: HttpResponse) = {
    response.status mustEqual StatusCodes.OK
    response.entity.asInstanceOf[HttpEntity.Strict].data.utf8String mustEqual "copied"
  }

  def movieNames = {
    val listRequest = HttpRequest(uri = s"http://${source.getHostString}:${source.getPort}/movies/list")
    
    Source(Http().singleRequest(listRequest))
      .map(_.entity.dataBytes.map(_.utf8String))
      .flatten(FlattenStrategy.concat)
      .take(4)
  }


  override protected def afterAll() = {
    await(sourceBinding.unbind())
    await(destinationBinding.unbind())
    sourceServer.actorConfigs.system.shutdown()
    destinationServer.actorConfigs.system.shutdown()
    system.shutdown()
  }
}
