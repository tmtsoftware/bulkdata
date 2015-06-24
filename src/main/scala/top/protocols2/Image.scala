package top.protocols2

import akka.stream.scaladsl.{Flow, BidiFlow}
import akka.util.ByteString

case class Image(id: String) {
  def updated = Image(s"Hello $id")
}

object Image {
  import scala.pickling.Defaults._
  import scala.pickling.binary._

  val codec = BidiFlow(Image.toBytes _, Image.fromBytes _)
  val stack = codec.atop(Framing.value)

  def fromInt(x: Int) = Image(x.toString)

  def toBytes(image: Image) = ByteString(image.pickle.value)

  def fromBytes(byteString: ByteString) = toBinaryPickle(byteString.toArray).unpickle[Image]

  def outbound = Flow[Image].map(Image.toBytes).map(Framing.addLengthHeader)
  def inbound = Flow[ByteString].transform(() => new FrameParser).map(Image.fromBytes)
}
