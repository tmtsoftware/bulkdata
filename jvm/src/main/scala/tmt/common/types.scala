package tmt.common

import akka.http.scaladsl.model.{HttpResponse, HttpRequest}
import akka.stream.scaladsl.Flow
import tmt.http.Handler

import scala.concurrent.Future

object Types {
  type RequestHandler = HttpRequest => Future[HttpResponse]
  type ConnectionFlow = Flow[HttpRequest, HttpResponse, Any]
}
