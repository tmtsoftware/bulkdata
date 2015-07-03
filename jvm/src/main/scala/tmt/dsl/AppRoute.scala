package tmt.dsl

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.{Sink, Source}
import tmt.common.Box

class AppRoute(boxService: BoxService, imageService: ImageService) extends CustomMarshallers with CustomDirectives {

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
        handleWebsocketMessages(Sink.ignore, imageService.send) ~
        complete(boxService.send)
      } ~
      post {
        entity(as[Source[Box, Any]]) { images =>
          onSuccess(boxService.copy(images)) {
            complete("copied")
          }
        }
      }
    } ~
    path("images" / "bidi") {
      post {
        entity(as[Source[Box, Any]]) { images =>
          complete(boxService.transform(images))
        }
      }
    }
  }
}
