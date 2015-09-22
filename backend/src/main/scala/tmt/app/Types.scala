package tmt.app

import akka.http.scaladsl.marshalling.Marshaller
import akka.http.scaladsl.model.HttpEntity.Chunked
import akka.http.scaladsl.model.{HttpResponse, HttpRequest}
import akka.stream.scaladsl.{Source, Flow}

import scala.concurrent.Future

object Types {
  type RequestHandler = HttpRequest => Future[HttpResponse]
  type ConnectionFlow = Flow[HttpRequest, HttpResponse, Any]
  type Stream[T] = Marshaller[Source[T, Any], Chunked]
}
