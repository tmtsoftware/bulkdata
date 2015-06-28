package top.common

import akka.util.ByteString

object ImageConversions {
  def toBytes(image: Image) = ByteString(Image.toBytes(image))
  def fromBytes(byteString: ByteString) =  Image.fromBytes(byteString.toByteBuffer)
}
