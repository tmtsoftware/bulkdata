package tmt.dsl

import java.net.InetSocketAddress

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{MessageEntity, HttpRequest, HttpMethods}
import tmt.common.ActorConfigs

import scala.concurrent.duration.DurationInt

class SimpleTransfer(source: InetSocketAddress, destination: InetSocketAddress, actorConfigs: ActorConfigs) {

  import actorConfigs._

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
