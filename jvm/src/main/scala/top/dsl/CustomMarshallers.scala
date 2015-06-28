package top.dsl

import akka.http.scaladsl.marshalling.Marshaller
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.unmarshalling.Unmarshaller
import akka.stream.scaladsl.Source
import akka.util.ByteString
import top.common.Image

trait CustomMarshallers {

  implicit val imageMarshaller = marshaller[Image](Image.toBytes)
  implicit val imageUnmarshaller = unmarshaller[Image](Image.fromBytes)

  def marshaller[T](toBytes: T => ByteString) = Marshaller.opaque { source: Source[T, Any] =>
    val byteStrings = source.map(toBytes)
    HttpEntity.Chunked.fromData(ContentTypes.`application/octet-stream`, byteStrings)
  }

  def unmarshaller[T](fromBytes: ByteString => T) = Unmarshaller.strict { entity: HttpEntity =>
    entity.dataBytes.map(fromBytes)
  }
}
