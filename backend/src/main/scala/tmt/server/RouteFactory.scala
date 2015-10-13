package tmt.server

import akka.http.scaladsl.model.ws.BinaryMessage
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString
import tmt.app.{ActorConfigs, AppSettings, CustomDirectives, Types}
import tmt.library.SourceExtensions.RichSource
import tmt.marshalling.BFormat
import tmt.shared.models.Image

class RouteFactory(actorConfigs: ActorConfigs, publisher: Publisher, appSettings: AppSettings) extends CustomDirectives {

  import actorConfigs._

  def images(topic: String, dataSource: Source[Image, Any]): Route = {
    val multicast = dataSource.hotMulticast
    makeRoute(topic, multicast, multicast.map(x => ByteString(x.bytes)))
  }

  def generic[T: Types.Stream: BFormat](topic: String, dataSource: Source[T, Any]): Route = {
    val multicast = dataSource.hotMulticast
    makeRoute(topic, multicast, multicast)
  }

  private def makeRoute[T: Types.Stream, S: BFormat](
    topic: String,
    dataSource: Source[T, Any],
    socketSource: Source[S, Any]): Route = {

    publisher.publish(topic, dataSource)
    def messages = socketSource.map(x => BinaryMessage(BFormat[S].write(x))).hotMulticast
    path(topic) {
      get {
        handleWebsocketMessages(Sink.ignore, messages) ~ complete(dataSource)
      }
    }
  }
}
