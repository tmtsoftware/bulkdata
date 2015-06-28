package top.http

import akka.http.scaladsl.model._
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import top.common.{ImageConversions, Image, ImageData}

import scala.concurrent.{ExecutionContext, Future}

class Handler(implicit mat: Materializer, executor: ExecutionContext) {

  val requestHandler: HttpRequest => Future[HttpResponse] = {

    case HttpRequest(HttpMethods.GET, Uri.Path("/images"), _, _, _) =>
      val chunked = HttpEntity.Chunked.fromData(ContentTypes.NoContentType, ImageData.ten.map(ImageConversions.toBytes))
      Future.successful(HttpResponse(entity = chunked))

    case HttpRequest(HttpMethods.POST, Uri.Path("/images"), _, entity, _) =>
      val images = entity.dataBytes.map(ImageConversions.fromBytes).log("Server-Received")
      images.runWith(Sink.ignore).map(_ => HttpResponse(entity = "saved"))

    case HttpRequest(HttpMethods.POST, Uri.Path("/images/bidi"), _, entity, _) =>
      val images = entity.dataBytes.map(ImageConversions.fromBytes).log("Server-Received").map(_.updated)
      val chunked = HttpEntity.Chunked.fromData(ContentTypes.NoContentType, images.map(ImageConversions.toBytes))
      Future.successful(HttpResponse(entity = chunked))

    case _: HttpRequest =>
      Future.successful(HttpResponse(StatusCodes.NotFound, entity = "error"))
  }

}