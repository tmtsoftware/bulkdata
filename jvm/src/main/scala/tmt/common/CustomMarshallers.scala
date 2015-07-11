package tmt.common

import akka.http.scaladsl.marshalling.Marshaller
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.unmarshalling.Unmarshaller
import akka.stream.scaladsl.Source
import akka.util.ByteString
import boopickle.{Pickle, Unpickle}
import tmt.boxes.http.BoxConversions

trait CustomMarshallers {

  implicit val stringMarshaller = marshaller[String](x => ByteString(x))
  implicit val stringUnmarshaller = unmarshaller[String](x => x.utf8String)

  implicit val imageMarshaller = marshaller[Image](x => ByteString(Pickle.intoBytes(x)))
  implicit val imageUnmarshaller = unmarshaller[Image](x => Unpickle[Image].fromBytes(x.toByteBuffer))

  implicit val bytesMarshaller = marshaller[ByteString](identity)
  implicit val bytesUnmarshaller = unmarshaller[ByteString](identity)

  implicit val boxMarshaller = marshaller(BoxConversions.toByteString)
  implicit val boxUnmarshaller = unmarshaller(BoxConversions.fromByteString)

  def marshaller[T](toBytes: T => ByteString) = Marshaller.opaque { source: Source[T, Any] =>
    val byteStrings = source.map(toBytes)
    HttpEntity.Chunked.fromData(ContentTypes.NoContentType, byteStrings)
  }

  def unmarshaller[T](fromBytes: ByteString => T) = Unmarshaller.strict { entity: HttpEntity =>
    entity.dataBytes.map(fromBytes)
  }
}
