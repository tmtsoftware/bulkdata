package tmt.server

import akka.http.scaladsl.model.ws.BinaryMessage
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.scaladsl.{Sink, Source}
import tmt.app.{ActorConfigs, AppSettings, CustomDirectives, Types}
import tmt.library.SourceExtensions.RichSource
import tmt.marshalling.BFormat
import tmt.shared.models.Role

class RouteFactory(actorConfigs: ActorConfigs, publisher: Publisher, appSettings: AppSettings) extends CustomDirectives {

  import actorConfigs._

  def make[T: Types.Stream: BFormat](topic: String, dataSource: Source[T, Any]): Route = {
    val multicast = dataSource.hotMulticast
    make(topic, multicast, multicast)
  }

  def make[T: Types.Stream, S: BFormat](topic: String, dataSource: Source[T, Any], socketSource: Source[S, Any]): Route = {
    publisher.publish(topic, dataSource)
    def messages = socketSource.map(x => BinaryMessage(BFormat[S].write(x))).hotMulticast
    path(topic) {
      get {
        handleWebsocketMessages(Sink.ignore, messages) ~ complete(dataSource)
      }
    }
  }
}
