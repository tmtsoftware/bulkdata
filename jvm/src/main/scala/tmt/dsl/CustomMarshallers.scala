package tmt.dsl

import akka.http.scaladsl.marshalling.Marshaller
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.unmarshalling.Unmarshaller
import akka.stream.scaladsl.Source
import akka.util.ByteString
import boopickle.{Pickle, Unpickle}
import tmt.common.{BoxConversions, Image}

trait CustomMarshallers {

  implicit val imageMarshaller = marshaller[Image](x => ByteString(Pickle.intoBytes(x)))
  implicit val imageUnmarshaller = unmarshaller[Image](x => Unpickle[Image].fromBytes(x.toByteBuffer))

  implicit val bytesMarshaller = marshaller[Array[Byte]](ByteString.apply)
  implicit val bytesUnmarshaller = unmarshaller[Array[Byte]](_.toByteBuffer.array())

  implicit val boxMarshaller = marshaller(BoxConversions.toByteString)
  implicit val boxUnmarshaller = unmarshaller(BoxConversions.fromByteString)

  def marshaller[T](toBytes: T => ByteString) = Marshaller.opaque { source: Source[T, Any] =>
    val byteStrings = source.map(toBytes)
    HttpEntity.Chunked.fromData(ContentTypes.`application/octet-stream`, byteStrings)
  }

  def unmarshaller[T](fromBytes: ByteString => T) = Unmarshaller.strict { entity: HttpEntity =>
    entity.dataBytes.map(fromBytes)
  }
}
