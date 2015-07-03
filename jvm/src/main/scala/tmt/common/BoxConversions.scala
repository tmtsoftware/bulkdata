package tmt.common

import akka.util.ByteString
import boopickle.{Pickle, Unpickle}

object BoxConversions {
  def toByteString(box: Box) = ByteString(Pickle.intoBytes(box))
  def fromByteString(byteString: ByteString) = Unpickle[Box].fromBytes(byteString.toByteBuffer)
}
