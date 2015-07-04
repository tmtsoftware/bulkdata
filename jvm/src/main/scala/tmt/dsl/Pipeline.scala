package tmt.dsl

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpResponse, Uri, HttpMethods, HttpRequest}
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import akka.stream.scaladsl.{FlattenStrategy, Source, Flow}
import tmt.common.Types.ConnectionFlow

class Pipeline(imageService: ImageService, route: Route)(implicit system: ActorSystem, mat: Materializer) {

  val connectionFlow: ConnectionFlow = Flow[HttpRequest].map {
    case HttpRequest(HttpMethods.GET, Uri.Path("/pipelined/images"), _, _, _) =>
      imageService.sendResponses
    case req =>
      Source.single(req).mapAsync[HttpResponse](1)(Route.asyncHandler(route))
  }.flatten(FlattenStrategy.concat)

}
