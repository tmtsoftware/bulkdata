package tmt.library

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import akka.stream.Materializer
import tmt.library.SourceExtensions.RichSource

object ResponseExtensions {
  
  implicit class RichResponse(val response: HttpResponse) extends AnyVal {

    def multicastEntity(implicit mat: Materializer) = HttpEntity.Chunked.fromData(
      ContentTypes.`application/octet-stream`,
      response.entity.dataBytes.multicast
    )
  }

}
