package tmt.common

import akka.util.ByteString
import boopickle.{Pickle, Unpickle}

trait ImageMarshallers extends BinaryMarshallers {
  implicit val imageWrites = ByteStringWrites.make[Image](x => ByteString(Pickle.intoBytes(x)))
  implicit val imageReads = ByteStringReads.make(byteString => Unpickle[Image].fromBytes(byteString.toByteBuffer))
}

object ImageMarshallers extends ImageMarshallers
