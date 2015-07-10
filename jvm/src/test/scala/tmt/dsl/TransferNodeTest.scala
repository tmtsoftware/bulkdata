package tmt.dsl

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.scaladsl.{FlattenStrategy, Sink, Source}
import org.scalatest.{BeforeAndAfterAll, FunSuite, MustMatchers}
import tmt.common.ActorConfigs
import tmt.common.Utils._

import scala.concurrent.duration.DurationInt
import scala.util.Success

class TransferNodeTest extends FunSuite with MustMatchers with BeforeAndAfterAll {
  private val interface       = "localhost"
  private val sourcePort      = 7001
  private val destinationPort = 8001

  val transferConfigs = new ActorConfigs("Proxy")
  import transferConfigs._

  val sourceServer = new DataNode(interface, sourcePort)
  val destinationServer = new DataNode(interface, destinationPort)

  val sourceBinding = await(sourceServer.server.run())
  val destinationBinding = await(destinationServer.server.run())
  
  private val transferNode = new TransferNode(interface, sourcePort, interface, destinationPort)

  test("frame-bytes") {
    val transferResponse = await(transferNode.singleTransfer("/images/bytes"))
    verifyTransfer(transferResponse)
  }

  test("frame-objects") {
    val transferResponse = await(transferNode.singleTransfer("/images/objects"))
    verifyTransfer(transferResponse)
  }

  test("blob-basic") {
    val listRequest = HttpRequest(uri = s"http://$interface:$sourcePort/movies/list")
    val listResponse = Http().singleRequest(listRequest)

    val listEntity = await(listResponse).entity
    val movieNames = listEntity.dataBytes.map(_.utf8String).take(4)

    val result =
      movieNames.mapAsync(1) { name =>
        transferNode.singleTransfer(s"/movies/$name")
      }
      .map(verifyTransfer)
      .runWith(Sink.ignore)

    await(result, 3.minute)
  }
  
  test("blob-pipelined") {
    val sourceFlow = Http().cachedHostConnectionPool[String](interface, sourcePort)

    val pipeline = Source.single(HttpRequest(uri = "/movies/list") -> "list")
      .via(sourceFlow)
      .collect { case (Success(response), "list") =>
        response.status mustEqual StatusCodes.OK
        response.entity.dataBytes.map(_.utf8String)
      }
      .flatten(FlattenStrategy.concat)
      .map(name => s"/movies/$name")
      .take(4)
      .via(transferNode.pipelinedTransfer)
      .map(verifyTransfer)
      .runWith(Sink.ignore)

    await(pipeline, 3.minute)
  }

  def verifyTransfer(response: HttpResponse) = {
    response.status mustEqual StatusCodes.OK
    response.entity.asInstanceOf[HttpEntity.Strict].data.utf8String mustEqual "copied"
  }

  override protected def afterAll() = {
    await(sourceBinding.unbind())
    await(destinationBinding.unbind())
    sourceServer.actorConfigs.system.shutdown()
    destinationServer.actorConfigs.system.shutdown()
    system.shutdown()
  }
}
