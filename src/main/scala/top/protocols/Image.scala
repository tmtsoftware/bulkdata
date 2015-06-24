package top.protocols

import akka.stream.scaladsl.{BidiFlow, Flow, Source}
import akka.util.ByteString

import scala.concurrent.duration._

case class Image(id: String) {
  def updated = Image(s"Hello $id")
}

object Image {
  import scala.pickling.Defaults._
  import scala.pickling.binary._

  val stream = Source(1.milli, 1.milli, ())
    .scan(1)((acc, _) => acc + 1)
    .map(x => Image(x.toString))

  val codec = BidiFlow(Image.toBytes _, Image.fromBytes _)
  val stack = codec.atop(Framing.bidi)

  val outbound = Flow[Image].map(Image.toBytes).map(Framing.addLengthHeader)
  val inbound = Flow[ByteString].transform(() => Framing.frameParser).map(Image.fromBytes)

  def toBytes(image: Image) = ByteString(image.pickle.value)
  def fromBytes(byteString: ByteString) = toBinaryPickle(byteString.toArray).unpickle[Image]
}
