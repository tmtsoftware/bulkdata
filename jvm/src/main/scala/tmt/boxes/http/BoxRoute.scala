package tmt.boxes.http

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.Source
import tmt.common.models.Box
import tmt.marshalling.BinaryMarshallers

class BoxRoute(boxService: BoxService) extends BinaryMarshallers {
  val route: Route = {
    path("boxes") {
      get {
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
    path("boxes" / "bidi") {
      post {
        entity(as[Source[Box, Any]]) { images =>
          complete(boxService.transform(images))
        }
      }
    }
  }
}
