package tmt.boxes.http

import akka.util.ByteString
import boopickle.{Unpickle, Pickle}
import tmt.common._

trait BoxMarshallers extends BinaryMarshallers {
  implicit val imageWrites = ByteStringWrites.make[Box](x => ByteString(Pickle.intoBytes(x)))
  implicit val imageReads = ByteStringReads.make(byteString => Unpickle[Box].fromBytes(byteString.toByteBuffer))
}

object BoxMarshallers extends BoxMarshallers
