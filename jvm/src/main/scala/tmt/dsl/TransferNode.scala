package tmt.dsl

import java.net.InetSocketAddress

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.scaladsl.Flow
import tmt.common.ActorConfigs

import scala.concurrent.duration._
import scala.util.Success

class TransferNode(source: InetSocketAddress, destination: InetSocketAddress) {

  val transferConfigs = new ActorConfigs("Transfer")
  import transferConfigs._

  val sourceFlow = Http().cachedHostConnectionPool[String](source.getHostName, source.getPort)
  val destinationFlow = Http().cachedHostConnectionPool[String](destination.getHostName, destination.getPort)

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
    val sourceUri = s"http://${source.getHostName}:${source.getPort}$relativeUri"
    val destinationUri = s"http://${destination.getHostName}:${destination.getPort}$relativeUri"
    val sourceRequest = HttpRequest(uri = sourceUri)
    val sourceResponse = Http().singleRequest(sourceRequest)

    sourceResponse.flatMap { resp =>
      val sourceEntity = resp.entity.asInstanceOf[MessageEntity]
      val destinationRequest = HttpRequest(uri = destinationUri, method = HttpMethods.POST, entity = sourceEntity)
      Http().singleRequest(destinationRequest).flatMap(_.toStrict(1.second))
    }
  }

}
