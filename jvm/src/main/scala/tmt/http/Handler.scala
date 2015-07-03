package tmt.http

import akka.http.scaladsl.model._
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import tmt.common.{BoxConversions, Boxes}

import scala.concurrent.{ExecutionContext, Future}

class Handler(implicit mat: Materializer, executor: ExecutionContext) {

  val requestHandler: HttpRequest => Future[HttpResponse] = {

    case HttpRequest(HttpMethods.GET, Uri.Path("/images"), _, _, _) =>
      val chunked = HttpEntity.Chunked.fromData(ContentTypes.`application/octet-stream`, Boxes.ten.map(BoxConversions.toByteString))
      Future.successful(HttpResponse(entity = chunked))

    case HttpRequest(HttpMethods.POST, Uri.Path("/images"), _, entity, _) =>
      val images = entity.dataBytes.map(BoxConversions.fromByteString).log("Server-Received")
      images.runWith(Sink.ignore).map(_ => HttpResponse(entity = "saved"))

    case HttpRequest(HttpMethods.POST, Uri.Path("/images/bidi"), _, entity, _) =>
      val images = entity.dataBytes.map(BoxConversions.fromByteString).log("Server-Received").map(_.updated)
      val chunked = HttpEntity.Chunked.fromData(ContentTypes.`application/octet-stream`, images.map(BoxConversions.toByteString))
      Future.successful(HttpResponse(entity = chunked))

    case _: HttpRequest =>
      Future.successful(HttpResponse(StatusCodes.NotFound, entity = "error"))
  }

}
