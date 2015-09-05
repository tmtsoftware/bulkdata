package tmt.marshalling

import akka.http.scaladsl.marshalling.Marshaller
import akka.http.scaladsl.model.HttpEntity.Chunked
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.unmarshalling.Unmarshaller
import akka.stream.scaladsl.Source

trait BinaryMarshallers {
  implicit def byteStringMarshaller[T: ByteStringWrites]: Marshaller[Source[T, Any], Chunked] = Marshaller.opaque { source: Source[T, Any] =>
    val byteStrings = source.map(ByteStringWrites[T].writes)
    HttpEntity.Chunked.fromData(ContentTypes.`application/octet-stream`, byteStrings)
  }

  implicit def byteStringUnmarshaller[T: ByteStringReads]: Unmarshaller[HttpEntity, Source[T, Any]] = Unmarshaller.strict { entity: HttpEntity =>
    entity.dataBytes.map(ByteStringReads[T].reads)
  }
}
