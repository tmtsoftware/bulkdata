package tmt.dsl

import java.net.InetSocketAddress

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.scaladsl.Flow
import tmt.common.ActorConfigs

import scala.concurrent.duration.DurationInt
import scala.util.Success

class ProducingClient(address: InetSocketAddress)(implicit actorConfigs: ActorConfigs) {
  import actorConfigs._

  val connectionFlow = Http().cachedHostConnectionPool[String](address.getHostName, address.getPort)

  val producerFlow: Flow[String, (MessageEntity, String), Unit] = Flow[String]
    .map(uri => HttpRequest(uri = uri) -> uri)
    .via(connectionFlow)
    .collect {  case (Success(response), uri) if response.status == StatusCodes.OK =>
      response.entity.asInstanceOf[MessageEntity] -> uri
    }
}

class ConsumingClient(address: InetSocketAddress)(implicit actorConfigs: ActorConfigs) {
  import actorConfigs._

  val connectionFlow = Http().cachedHostConnectionPool[String](address.getHostName, address.getPort)

  val consumerFlow: Flow[(MessageEntity, String), HttpResponse, Any] = Flow[(MessageEntity, String)]
    .collect { case (entity, uri) =>
      HttpRequest(uri = uri, method = HttpMethods.POST, entity = entity) -> uri
    }
    .via(connectionFlow)
    .mapAsync(1) { case (Success(response), name) => response.toStrict(1.second) }

}
