package tmt.media.server

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString
import tmt.common._

class MediaRoute(
  mediaReadService: MediaReadService,
  mediaWriteService: MediaWriteService,
  settings: AppSettings
) extends CommonMarshallers with CustomDirectives {

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
        complete(mediaReadService.listMovies)
      }
    } ~
    pathPrefix("movies") {
      path(Rest) { name =>
        get {
          complete(mediaReadService.sendMovie(name))
        } ~
        post {
          entity(as[Source[ByteString, Any]]) { byteStrings =>
            onSuccess(mediaWriteService.copyMovie(name, byteStrings)) {
              complete("copied")
            }
          }
        }
      }
    } ~
    path("images" / "bytes") {
      get {
        handleWebsocketMessages(Sink.ignore, mediaReadService.sendMessages) ~
        complete(mediaReadService.sendBytes)
      } ~
      post {
        entity(as[Source[ByteString, Any]]) { byteStrings =>
          onSuccess(mediaWriteService.copyBytes(byteStrings)) {
            complete("copied")
          }
        }
      }
    } ~
    path("images" / "objects") {
      get {
        complete(mediaReadService.sendImages)
      } ~
      post {
        entity(as[Source[Image, Any]]) { images =>
          onSuccess(mediaWriteService.copyImages(images)) {
            complete("copied")
          }
        }
      }
    }
  }
}
