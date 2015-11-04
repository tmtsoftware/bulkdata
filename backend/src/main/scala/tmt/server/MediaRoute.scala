package tmt.server

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString
import tmt.app.CustomDirectives
import tmt.io.{WavefrontReadService, WavefrontWriteService, ScienceImageReadService, ScienceImageWriteService}
import tmt.marshalling.BinaryMarshallers
import tmt.shared.models.Image

class MediaRoute(
  wavefrontReadService: WavefrontReadService,
  wavefrontWriteService: WavefrontWriteService,
  scienceImageReadService: ScienceImageReadService,
  scienceImageWriteService: ScienceImageWriteService
) extends BinaryMarshallers with CustomDirectives {

  val route = movieRoute ~ imageRoute

  lazy val movieRoute: Route = {
    path("science-images" / "list") {
      get {
        complete(scienceImageReadService.listScienceImages)
      }
    } ~
      pathPrefix("science-images") {
        path(Rest) { name =>
          get {
            complete(scienceImageReadService.send(name))
          } ~
            post {
              entity(as[Source[ByteString, Any]]) { byteStrings =>
                onSuccess(scienceImageWriteService.copy(name, byteStrings)) {
                  complete("copied")
                }
              }
            }
        }
      }
  }

  lazy val imageRoute: Route = {
    path("images" / "bytes") {
      get {
        handleWebsocketMessages(Sink.ignore, wavefrontReadService.sendMessages) ~
        complete(wavefrontReadService.sendBytes)
      } ~
      post {
        entity(as[Source[ByteString, Any]]) { byteStrings =>
          onSuccess(wavefrontWriteService.copyBytes(byteStrings)) {
            complete("copied")
          }
        }
      }
    } ~
    path("images" / "objects") {
      get {
        complete(wavefrontReadService.sendImages)
      } ~
      post {
        entity(as[Source[Image, Any]]) { images =>
          onSuccess(wavefrontWriteService.copyImages(images)) {
            complete("copied")
          }
        }
      }
    }
  }
}
