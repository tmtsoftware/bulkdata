package top.dsl

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.{Sink, Source}
import top.common.Box

class AppRoute(boxService: BoxService, imageService: ImageService) extends CustomMarshallers with CustomDirectives {

  val route: Route = {

    pathSingleSlash {
      complete("home")
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
