package tmt.wavefront

import akka.http.scaladsl.model.ws.BinaryMessage
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.scaladsl.{Sink, Source}
import tmt.common.{CustomDirectives, Types, ActorConfigs}
import tmt.library.SourceExtensions.RichSource
import tmt.marshalling.BFormat

class RouteFactory(actorConfigs: ActorConfigs) extends CustomDirectives {
  import actorConfigs._

  def make[T: Types.Stream](routeName: String, dataSource: Source[T, Any]): Route = {
    val items = dataSource.hotMulticast
    path(routeName) {
      get(complete(items.hotUnicast))
    }
  }

  def websocket[T: BFormat](routeName: String, dataSource: Source[T, Any]): Route = {
    val items = dataSource.hotMulticast
    val messages = items.map(x => BinaryMessage(BFormat[T].write(x)))
    path(routeName) {
      handleWebsocketMessages(Sink.ignore, messages.hotUnicast)
    }
  }
}
