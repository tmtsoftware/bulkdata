package tmt.dsl

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.{Sink, Source}

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
    path("images") {
      get {
        handleWebsocketMessages(Sink.ignore, mediaService.sendMessages) ~
        complete(mediaService.sendBytes)
      } ~
      post {
        entity(as[Source[Array[Byte], Any]]) { byteArrays =>
          onSuccess(mediaService.copy(byteArrays)) {
            complete("copied")
          }
        }
      }
    }
  }
}
