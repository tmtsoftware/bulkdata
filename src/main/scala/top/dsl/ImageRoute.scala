package top.dsl

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.scaladsl.Source
import top.common.Image

class ImageRoute(imageService: ImageService) extends ImageMarshalling {

  val route: Route = {

    pathPrefix("images") {
      pathEnd {
        get {
          complete(imageService.read)
        } ~
        post {
          entity(as[Source[Image, Any]]) { images =>
            onSuccess(imageService.copy(images)) {
              complete("saved")
            }
          }
        }
      } ~
      path("bidi") {
        post {
          entity(as[Source[Image, Any]]) { images =>
            complete(imageService.transform(images))
          }
        }
      }
    }

  }
}
