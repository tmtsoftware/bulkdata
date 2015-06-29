package top.dsl

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.Source
import top.common.Image

class ImageRoute(imageService: ImageService) extends CustomMarshallers {

  val route: Route = {

    pathSingleSlash {
      complete("home")
    } ~
    path("images") {
      get {
        handleWebsocketMessages(imageService.sendRealImages) ~
        complete(imageService.images)
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
