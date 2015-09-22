package tmt.library

import akka.http.scaladsl.model.HttpRequest
import akka.stream.scaladsl.Flow
import tmt.app.Types

object RequestHandlerExtensions {

  implicit class RichRequestHandler(val requestHandler: Types.RequestHandler) extends AnyVal {
    def toFlow = Flow[HttpRequest].mapAsync(1)(requestHandler)
  }

}
