package tmt.media

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString
import tmt.common._

class MediaRoute(mediaService: MediaService, configs: ActorConfigs, settings: AppSettings) extends CommonMarshallers with CustomDirectives {

  val route: Route = {

    pathSingleSlash {
      getFromResource("web/index.html")
    } ~
    path("data-transfer-launcher.js") {
      getFromResource("data-transfer-launcher.js")
    } ~
    path("data-transfer-fastopt.js") {
      getFromResource("data-transfer-fastopt.js")
    } ~
    path("movies" / "list") {
      get {
        complete(mediaService.listMovies)
      }
    } ~
    pathPrefix("movies") {
      getFromDirectory(settings.moviesInputDir) ~
      path(Rest) { name =>
        post {
          entity(as[Source[ByteString, Any]]) { byteStrings =>
            onSuccess(mediaService.copyMovie(name, byteStrings)) {
              complete("copied")
            }
          }
        }
      }
    } ~
    path("images" / "bytes") {
      get {
        handleWebsocketMessages(Sink.ignore, mediaService.sendMessages) ~
        complete(mediaService.sendBytes)
      } ~
      post {
        entity(as[Source[ByteString, Any]]) { byteStrings =>
          onSuccess(mediaService.copyBytes(byteStrings)) {
            complete("copied")
          }
        }
      }
    } ~
    path("images" / "objects") {
      get {
        complete(mediaService.sendImages)
      } ~
      post {
        entity(as[Source[Image, Any]]) { images =>
          onSuccess(mediaService.copyImages(images)) {
            complete("copied")
          }
        }
      }
    }
  }
}
