package tmt.dsl

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.Materializer
import akka.stream.scaladsl.Flow

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.Success

class TransferNode(sourceHost: String, sourcePort: Int, destinationHost: String, destinationPort: Int)(
  implicit system: ActorSystem, ec: ExecutionContext, mat: Materializer
) {

  val sourceFlow = Http().cachedHostConnectionPool[String](sourceHost, sourcePort)
  val destinationFlow = Http().cachedHostConnectionPool[String](destinationHost, destinationPort)

  val pipelinedTransfer: Flow[String, HttpResponse, Any] = Flow[String]
    .map(uri => HttpRequest(uri = uri) -> uri)
    .via(sourceFlow)
    .collect { case (Success(response), uri) =>
      val getEntity = response.entity.asInstanceOf[MessageEntity]
      HttpRequest(uri = uri, method = HttpMethods.POST, entity = getEntity) -> uri
    }
    .via(destinationFlow)
    .mapAsync(1) { case (Success(response), name) => response.toStrict(1.second) }

  def singleTransfer(relativeUri: String) = {
    val sourceUri = s"http://$sourceHost:$sourcePort$relativeUri"
    val destinationUri = s"http://$destinationHost:$destinationPort$relativeUri"
    val sourceRequest = HttpRequest(uri = sourceUri)
    val sourceResponse = Http().singleRequest(sourceRequest)

    sourceResponse.flatMap { resp =>
      val sourceEntity = resp.entity.asInstanceOf[MessageEntity]
      val destinationRequest = HttpRequest(uri = destinationUri, method = HttpMethods.POST, entity = sourceEntity)
      Http().singleRequest(destinationRequest).flatMap(_.toStrict(1.second))
    }
  }

}
