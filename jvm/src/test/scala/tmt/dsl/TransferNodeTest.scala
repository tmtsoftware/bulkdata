package tmt.dsl

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.scaladsl.{FlattenStrategy, Sink, Source}
import org.scalatest.{BeforeAndAfterAll, FunSuite, MustMatchers}
import tmt.common.ActorConfigs
import tmt.common.Utils._

import scala.concurrent.duration.DurationInt

class TransferNodeTest extends FunSuite with MustMatchers with BeforeAndAfterAll {
  private val interface       = "localhost"
  private val sourcePort      = 7001
  private val destinationPort = 8001

  val testConfigs = new ActorConfigs("Test")
  import testConfigs._

  val sourceServer = new DataNode(interface, sourcePort)
  val destinationServer = new DataNode(interface, destinationPort)
  val transferNode = new TransferNode(interface, sourcePort, interface, destinationPort)

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
    val result = movieList
      .mapAsync(1) { name => transferNode.singleTransfer(s"/movies/$name") }
      .map(verifyTransfer)
      .runWith(Sink.ignore)

    await(result, 3.minute)
  }
  
  test("blob-pipelined") {
    val result = movieList
      .via(transferNode.pipelinedTransfer)
      .map(verifyTransfer)
      .runWith(Sink.ignore)

    await(result, 3.minute)
  }

  def verifyTransfer(response: HttpResponse) = {
    response.status mustEqual StatusCodes.OK
    response.entity.asInstanceOf[HttpEntity.Strict].data.utf8String mustEqual "copied"
  }

  def movieList = {
    val listRequest = HttpRequest(uri = s"http://$interface:$sourcePort/movies/list")

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
