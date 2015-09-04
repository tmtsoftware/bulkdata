package tmt.common

import akka.util.ByteString
import boopickle.{Pickle, Unpickle}

trait CommonMarshallers extends SourceMarshallers {
  implicit val stringMarshaller = marshaller[String](x => ByteString(x))
  implicit val stringUnmarshaller = unmarshaller[String](x => x.utf8String)

  implicit val imageMarshaller = marshaller[Image](ImageConverter.toByteString)
  implicit val imageUnmarshaller = unmarshaller[Image](ImageConverter.fromByteString)

  implicit val bytesMarshaller = marshaller[ByteString](identity)
  implicit val bytesUnmarshaller = unmarshaller[ByteString](identity)
}

object ImageConverter {
  def fromByteString(byteString: ByteString) = Unpickle[Image].fromBytes(byteString.toByteBuffer)
  def toByteString(image: Image) = ByteString(Pickle.intoBytes(image))
}
