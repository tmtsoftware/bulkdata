package tmt.dsl

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.{Sink, Source}
import tmt.common.Image

class MediaRoute(mediaService: MediaService) extends CustomMarshallers with CustomDirectives {

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
    path("images" / "bytes") {
      get {
        handleWebsocketMessages(Sink.ignore, mediaService.sendMessages) ~
        complete(mediaService.sendBytes)
      } ~
      post {
        entity(as[Source[Array[Byte], Any]]) { byteArrays =>
          onSuccess(mediaService.copyBytes(byteArrays)) {
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
