package tmt.server

import akka.http.scaladsl.model.ws.BinaryMessage
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.scaladsl.{Sink, Source}
import tmt.library.Role
import tmt.app.{ActorConfigs, AppSettings, CustomDirectives, Types}
import tmt.library.SourceExtensions.RichSource
import tmt.marshalling.BFormat

class RouteFactory(actorConfigs: ActorConfigs, publisher: Publisher, appSettings: AppSettings) extends CustomDirectives {

  import actorConfigs._

  def make[T: Types.Stream: BFormat](role: Role, dataSource: Source[T, Any]): Route =
    make(role, dataSource, dataSource)

  def make[T: Types.Stream, S: BFormat](role: Role, dataSource: Source[T, Any], socketSource: Source[S, Any]): Route = {
    publisher.publish(role, dataSource)
    def messages = socketSource.map(x => BinaryMessage(BFormat[S].write(x))).hotMulticast
    path(role.entryName) {
      get {
        handleWebsocketMessages(Sink.ignore, messages) ~ complete(dataSource)
      }
    }
  }
}
