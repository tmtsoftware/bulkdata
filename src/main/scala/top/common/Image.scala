package top.common

import akka.util.ByteString

case class Image(id: String) {
  def updated = Image(s"Hello $id")
}

object Image {
  import boopickle._

  def toBytes(image: Image) = ByteString(Pickle.intoBytes(image))
  def fromBytes(byteString: ByteString) =  Unpickle[Image].fromBytes(byteString.toByteBuffer)
}
