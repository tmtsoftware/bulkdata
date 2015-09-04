package tmt.common

import akka.http.scaladsl.marshalling.Marshaller
import akka.http.scaladsl.model.HttpEntity.Chunked
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.unmarshalling.Unmarshaller
import akka.stream.scaladsl.Source
import akka.util.ByteString
import play.api.libs.json.{Json, Reads, Writes}

trait JsonMarshallers {
  implicit def jsonMarshaller[T: Writes]: Marshaller[Source[T, Any], Chunked] = Marshaller.opaque { source: Source[T, Any] =>
    val byteStrings = source.map(x => ByteString(implicitly[Writes[T]].writes(x).toString()))
    HttpEntity.Chunked.fromData(ContentTypes.`application/json`, byteStrings)
  }

  implicit def jsonUnmarshaller[T: Reads]: Unmarshaller[HttpEntity, Source[T, Any]] = Unmarshaller.strict { entity: HttpEntity =>
    entity.dataBytes.map(x => Json.parse(x.utf8String).as[T])
  }
}
