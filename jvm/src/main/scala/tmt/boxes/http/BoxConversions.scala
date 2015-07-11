package tmt.boxes.http

import akka.util.ByteString
import boopickle.{Pickle, Unpickle}
import tmt.common.Box

object BoxConversions {
  def toByteString(box: Box) = ByteString(Pickle.intoBytes(box))
  def fromByteString(byteString: ByteString) = Unpickle[Box].fromBytes(byteString.toByteBuffer)
}
