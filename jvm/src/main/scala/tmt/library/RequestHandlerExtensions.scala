package tmt.library

import akka.http.scaladsl.model.HttpRequest
import akka.stream.scaladsl.Flow
import tmt.common.Types.RequestHandler

object RequestHandlerExtensions {

  implicit class RichRequestHandler(val requestHandler: RequestHandler) extends AnyVal {
    def toFlow = Flow[HttpRequest].mapAsync(1)(requestHandler)
  }

}
