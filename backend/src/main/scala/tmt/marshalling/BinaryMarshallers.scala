package tmt.marshalling

import akka.http.scaladsl.marshalling.Marshaller
import akka.http.scaladsl.model.HttpEntity.Chunked
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.unmarshalling.Unmarshaller
import akka.stream.scaladsl.Source

trait BinaryMarshallers {
  implicit def byteStringMarshaller[T: BFormat]: Marshaller[Source[T, Any], Chunked] = Marshaller.opaque { source: Source[T, Any] =>
    val byteStrings = source.map(BFormat[T].write)
    HttpEntity.Chunked.fromData(ContentTypes.`application/octet-stream`, byteStrings)
  }

  implicit def byteStringUnmarshaller[T: BFormat]: Unmarshaller[HttpEntity, Source[T, Any]] = Unmarshaller.strict { entity: HttpEntity =>
    entity.dataBytes.map(BFormat[T].read)
  }
}
