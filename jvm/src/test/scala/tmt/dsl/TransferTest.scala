package tmt.dsl

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.scaladsl.{FlattenStrategy, Sink, Source}
import org.scalatest.{BeforeAndAfterAll, FunSuite, MustMatchers}
import tmt.common.ActorConfigs
import tmt.common.Utils._

import scala.concurrent.duration.DurationInt
import scala.util.Success

class TransferTest extends FunSuite with MustMatchers with BeforeAndAfterAll {
  private val interface       = "localhost"
  private val sourcePort      = 7001
  private val destinationPort = 8001

  val transferConfigs = new ActorConfigs("Proxy")
  import transferConfigs._

  val sourceServer = new DataNode(interface, sourcePort)
  val destinationServer = new DataNode(interface, destinationPort)

  val sourceBinding = await(sourceServer.server.run())
  val destinationBinding = await(destinationServer.server.run())

  test("frame-bytes") {
    await(transfer(
      sourceUri = s"http://$interface:$sourcePort/images/bytes",
      destinationUri = s"http://$interface:$destinationPort/images/bytes"
    ))
  }

  test("frame-objects") {
    await(transfer(
      sourceUri = s"http://$interface:$sourcePort/images/objects",
      destinationUri = s"http://$interface:$destinationPort/images/objects"
    ))
  }

  test("blob-basic") {
    val listRequest = HttpRequest(uri = s"http://$interface:$sourcePort/movies/list")
    val listResponse = Http().singleRequest(listRequest)

    val listEntity = await(listResponse).entity
    val movieNames = listEntity.dataBytes.map(_.utf8String).take(4)

    val result = movieNames.mapAsync(1) { name =>
      transfer(
        sourceUri = s"http://$interface:$sourcePort/movies/$name",
        destinationUri = s"http://$interface:$destinationPort/movies/$name"
      )
    }.runWith(Sink.ignore)

    await(result, 3.minute)
  }

  def transfer(sourceUri: String, destinationUri: String) = {
    val sourceRequest = HttpRequest(uri = sourceUri)
    val sourceResponse = Http().singleRequest(sourceRequest)

    sourceResponse.flatMap { resp =>
      val sourceEntity = resp.entity.asInstanceOf[MessageEntity]
      val destinationRequest = HttpRequest(uri = sourceUri, method = HttpMethods.POST, entity = sourceEntity)
      Http().singleRequest(destinationRequest) flatMap { destinationResponse =>
        destinationResponse.status mustEqual StatusCodes.OK
        destinationResponse.entity.toStrict(1.seconds) map { strictEntity =>
          strictEntity.data.utf8String mustEqual "copied"
        }
      }
    }
  }

  test("blob-pipelined") {
    val sourceFlow = Http().newHostConnectionPool[String](interface, sourcePort)
    val destinationFlow = Http().newHostConnectionPool[String](interface, destinationPort)

    val pipeline = Source.single(HttpRequest(uri = "/movies/list") -> "list")
      .via(sourceFlow)
      .collect {
        case (Success(response), "list") =>
          response.status mustEqual StatusCodes.OK
          response.entity.dataBytes.map(_.utf8String)
      }
      .flatten(FlattenStrategy.concat)
      .take(4)
      .map(name => HttpRequest(uri = s"/movies/$name") -> name)
      .via(sourceFlow)
      .collect {
        case (Success(response), name) =>
          val getEntity = response.entity.asInstanceOf[MessageEntity]
          HttpRequest(uri = s"/movies/$name", method = HttpMethods.POST, entity = getEntity) -> name
      }
      .via(destinationFlow)
      .mapAsync(1) {
        case (Success(response), name) =>
          response.status mustEqual StatusCodes.OK
          response.entity.toStrict(1.seconds)
      }
      .map(_.data.utf8String mustEqual "copied")
      .runWith(Sink.ignore)

    await(pipeline, 3.minute)
  }


  override protected def afterAll() = {
    await(sourceBinding.unbind())
    await(destinationBinding.unbind())
    sourceServer.actorConfigs.system.shutdown()
    destinationServer.actorConfigs.system.shutdown()
    system.shutdown()
  }
}
