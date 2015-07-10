package tmt.dsl

import java.net.InetSocketAddress

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.scaladsl.Flow
import tmt.common.ActorConfigs

import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

class ProducingClient(address: InetSocketAddress)(implicit actorConfigs: ActorConfigs) {
  import actorConfigs._

  val connectionFlow = Http().superPool[Uri]()

  val producerFlow: Flow[String, (MessageEntity, Uri), Unit] = Flow[String]
    .map(A.dd(address, _))
    .map(uri => HttpRequest(uri = uri) -> uri)
    .via(connectionFlow)
    .collect {  case (Success(response), uri) if response.status == StatusCodes.OK =>
      response.entity.asInstanceOf[MessageEntity] -> uri
    }
}

class ConsumingClient(address: InetSocketAddress)(implicit actorConfigs: ActorConfigs) {
  import actorConfigs._

  val connectionFlow = Http().superPool[Uri]()

  val consumerFlow: Flow[(MessageEntity, Uri), HttpResponse, Any] = Flow[(MessageEntity, Uri)]
    .collect { case (entity, uri) =>
      println("**************", uri, A.ee(address, uri))
      HttpRequest(uri = A.ee(address, uri), method = HttpMethods.POST, entity = entity) -> A.ee(address, uri)
    }
    .via(connectionFlow)
    .mapAsync(1) {
      case (Success(response), name) =>
        println("**************", name)
        response.toStrict(1.second)
      case (Failure(ex), name) =>
        println("^^^^^^^^^^^^^^", ex.getMessage, name)
        throw ex
    }

}

object A {
  def dd(address: InetSocketAddress, relativeUri: String) = Uri(s"http://${address.getHostName}:${address.getPort}$relativeUri")
  def ee(address: InetSocketAddress, uri: Uri) = uri.withAuthority(address.getHostName, address.getPort)

}