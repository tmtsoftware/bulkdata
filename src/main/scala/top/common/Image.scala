package top.common

import akka.util.ByteString

case class Image(id: String) {
  def updated = Image(s"Hello $id")
}

object Image {
  import scala.pickling.Defaults._
  import scala.pickling.binary._

  def toBytes(image: Image) = ByteString(image.pickle.value)
  def fromBytes(byteString: ByteString) = toBinaryPickle(byteString.toArray).unpickle[Image]
}
