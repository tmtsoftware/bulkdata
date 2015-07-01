package top.common

import java.nio.ByteBuffer

import boopickle._

case class Box(id: String) {
  def updated = Box(s"Hello $id")
}

object Box {
  def toByteBuffer(box: Box): ByteBuffer = Pickle.intoBytes(box)
  def fromByteBuffer(bytes: ByteBuffer) =  Unpickle[Box].fromBytes(bytes)
}

case class Image(
  name: String,
  mimeType: String,
  width: Int,
  height: Int,
  bytes: Array[Byte]
) {
  def aspect = width.toDouble / height
  def scaledHeight(w: Int) = (w/aspect).toInt
}

object Image {
  def toByteBuffer(image: Image): ByteBuffer = Pickle.intoBytes(image)
  def fromByteBuffer(bytes: ByteBuffer) =  Unpickle[Image].fromBytes(bytes)
}
