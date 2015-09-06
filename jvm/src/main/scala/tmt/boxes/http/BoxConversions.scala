package tmt.boxes.http

import akka.util.ByteString
import boopickle.Default._
import tmt.common.models.Box

object BoxConversions {
  def toByteString(box: Box) = ByteString(Pickle.intoBytes(box))
  def fromByteString(byteString: ByteString) = Unpickle[Box].fromBytes(byteString.toByteBuffer)
}
