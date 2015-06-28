package top.common

import java.nio.ByteBuffer

case class Image(id: String) {
  def updated = Image(s"Hello $id")
}

object Image {
  import boopickle._

  def toBytes(image: Image): ByteBuffer = Pickle.intoBytes(image)
  def fromBytes(bytes: ByteBuffer) =  Unpickle[Image].fromBytes(bytes)
}
