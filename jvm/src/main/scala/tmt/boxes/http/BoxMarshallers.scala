package tmt.boxes.http

import akka.util.ByteString
import boopickle.Default._
import tmt.common._
import tmt.marshalling.{ByteStringWrites, ByteStringReads, BinaryMarshallers}

trait BoxMarshallers extends BinaryMarshallers {
  implicit val imageWrites = ByteStringWrites.make[Box](x => ByteString(Pickle.intoBytes(x)))
  implicit val imageReads = ByteStringReads.make(byteString => Unpickle[Box].fromBytes(byteString.toByteBuffer))
}

object BoxMarshallers extends BoxMarshallers
