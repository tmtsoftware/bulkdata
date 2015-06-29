package top.common

import java.nio.ByteBuffer

import boopickle._

case class Image(id: String) {
  def updated = Image(s"Hello $id")
}

object Image {
  def toByteBuffer(image: Image): ByteBuffer = Pickle.intoBytes(image)
  def fromByteBuffer(bytes: ByteBuffer) =  Unpickle[Image].fromBytes(bytes)
}

case class RealImage(
  name: String,
  mimeType: String,
  width: Int,
  height: Int,
  bytes: Array[Byte]
) {
  def aspect = width.toDouble / height
  def scaledHeight(w: Int) = (w/aspect).toInt
}

object RealImage {
  def toByteBuffer(image: RealImage): ByteBuffer = Pickle.intoBytes(image)
  def fromByteBuffer(bytes: ByteBuffer) =  Unpickle[RealImage].fromBytes(bytes)
}
