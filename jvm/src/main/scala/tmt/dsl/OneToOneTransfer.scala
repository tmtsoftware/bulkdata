package tmt.dsl

import java.net.InetSocketAddress

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.scaladsl.Flow
import tmt.common.ActorConfigs

import scala.concurrent.duration._
import scala.util.Success

class OneToOneTransfer(source: InetSocketAddress, destination: InetSocketAddress)(implicit actorConfigs: ActorConfigs) {

  import actorConfigs._

  private val producingClient = new ProducingClient(source)
  private val consumingClient = new ConsumingClient(destination)
  
  val transferFlow = producingClient.flow.via(consumingClient.flow)
  
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
