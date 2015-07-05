package tmt.dsl

import akka.http.scaladsl.marshalling.Marshaller
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.unmarshalling.Unmarshaller
import akka.stream.scaladsl.Source
import akka.util.ByteString
import tmt.common.{Box, BoxConversions}

trait CustomMarshallers {

  implicit val bytesMarshaller = marshaller[Array[Byte]](ByteString.apply)

  implicit val boxMarshaller = marshaller[Box](BoxConversions.toByteString)
  implicit val boxUnmarshaller = unmarshaller[Box](BoxConversions.fromByteString)

  def marshaller[T](toBytes: T => ByteString) = Marshaller.opaque { source: Source[T, Any] =>
    val byteStrings = source.map(toBytes)
    HttpEntity.Chunked.fromData(ContentTypes.`application/octet-stream`, byteStrings)
  }

  def unmarshaller[T](fromBytes: ByteString => T) = Unmarshaller.strict { entity: HttpEntity =>
    entity.dataBytes.map(fromBytes)
  }
}
