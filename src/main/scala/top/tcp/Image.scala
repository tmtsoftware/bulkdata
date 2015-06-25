package top.tcp

import akka.stream.scaladsl.{BidiFlow, Flow, Source}
import akka.util.ByteString

import scala.concurrent.duration._

case class Image(id: String) {
  def updated = Image(s"Hello $id")
}

object Image {
  import scala.pickling.Defaults._
  import scala.pickling.binary._

  val stream = Source(1.second, 1.second, ())
    .scan(1)((acc, _) => acc + 1)
    .map(x => Image(x.toString))

  val ten = stream.take(10)

  val single = Source.single(Image(""))
  val lazyEmpty = Source.lazyEmpty[Image]

  val codec = BidiFlow(Image.toBytes _, Image.fromBytes _)
  val stack = codec.atop(Framer.codec)

  val outbound = Flow[Image].map(Image.toBytes).via(Framer.encoder)
  val inbound = Flow[ByteString].via(Framer.decoder).map(Image.fromBytes)

  def toBytes(image: Image) = ByteString(image.pickle.value)
  def fromBytes(byteString: ByteString) = toBinaryPickle(byteString.toArray).unpickle[Image]
}
