package top.common

import java.io.File
import java.nio.ByteBuffer
import java.nio.file.Files
import boopickle._

case class Image(id: String) {
  def updated = Image(s"Hello $id")
}

object Image {
  def toBytes(image: Image): ByteBuffer = Pickle.intoBytes(image)
  def fromBytes(bytes: ByteBuffer) =  Unpickle[Image].fromBytes(bytes)
}

case class RealImage(name: String, encoding: String, bytes: Array[Byte])

object RealImage {
  def fromFile(file: File) = RealImage(
    file.getName,
    "image/jpeg",
    Files.readAllBytes(file.toPath)
  )
  def toBytes(image: RealImage): ByteBuffer = Pickle.intoBytes(image)
  def fromBytes(bytes: ByteBuffer) =  Unpickle[RealImage].fromBytes(bytes)
}
