package top.dsl

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.{Sink, Source}
import top.common.Image

class ImageRoute(imageService: ImageService, frameService: FrameService) extends CustomMarshallers with CustomDirectives {

  val route: Route = {

    pathSingleSlash {
      complete("home")
    } ~
    path("images") {
      get {
        handleWebsocketMessages(Sink.ignore, frameService.send) ~
        complete(imageService.send)
      } ~
      post {
        entity(as[Source[Image, Any]]) { images =>
          onSuccess(imageService.copy(images)) {
            complete("copied")
          }
        }
      }
    } ~
    path("images" / "bidi") {
      post {
        entity(as[Source[Image, Any]]) { images =>
          complete(imageService.transform(images))
        }
      }
    }
  }
}
