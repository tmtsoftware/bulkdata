package top.protocols

import akka.stream.scaladsl.BidiFlow
import akka.util.ByteString

case class Image(id: String) {
  def updated = Image(s"Hello $id")
}

object Image {
  import scala.pickling.Defaults._
  import scala.pickling.binary._

  val codec = BidiFlow(Image.toBytes _, Image.fromBytes _)
  val stack = codec.atop(Framing.value)

  def toBytes(image: Image) = ByteString(image.pickle.value)

  def fromBytes(byteString: ByteString) = toBinaryPickle(byteString.toArray).unpickle[Image]
}
