package tmt.common

import akka.util.ByteString
import boopickle.{Pickle, Unpickle}

trait CommonMarshallers extends SourceMarshallers {
  implicit val stringMarshaller = marshaller[String](x => ByteString(x))
  implicit val stringUnmarshaller = unmarshaller[String](x => x.utf8String)

  implicit val imageMarshaller = marshaller[Image](x => ByteString(Pickle.intoBytes(x)))
  implicit val imageUnmarshaller = unmarshaller[Image](x => Unpickle[Image].fromBytes(x.toByteBuffer))

  implicit val bytesMarshaller = marshaller[ByteString](identity)
  implicit val bytesUnmarshaller = unmarshaller[ByteString](identity)
}
