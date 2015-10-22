package tmt.server

import akka.http.scaladsl.model.ws.BinaryMessage
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString
import prickle.Pickle
import tmt.app.{ActorConfigs, AppSettings, CustomDirectives, Types}
import tmt.io.ScienceImageReadService
import tmt.library.SourceExtensions.RichSource
import tmt.marshalling.{BinaryMarshallers, BFormat}
import tmt.shared.Topics
import tmt.shared.models.Image
//import prickle._

class RouteFactory(
  scienceImageReadService: ScienceImageReadService,
  actorConfigs: ActorConfigs,
  publisher: Publisher,
  appSettings: AppSettings) extends CustomDirectives with BinaryMarshallers {

  import actorConfigs._

  def scienceImages: Route = get {
    path(Topics.ScienceImages) {
      complete(Pickle.intoString(scienceImageReadService.movies))
    } ~
    pathPrefix(Topics.ScienceImages) {
      path(Rest) { name =>
        complete(scienceImageReadService.send(name))
      }
    }
  }

  def wavefront(topic: String, dataSource: Source[Image, Any]): Route = {
    val frames = dataSource.hotMulticast
    val bytes = frames.map(x => ByteString(x.bytes))
    makeRoute(topic, frames, bytes)
  }

  def generic[T: Types.Stream: BFormat](topic: String, dataSource: Source[T, Any]): Route = {
    val items = dataSource.hotMulticast
    makeRoute(topic, items, items)
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
