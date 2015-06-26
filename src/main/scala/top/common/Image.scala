package top.common

import akka.stream.scaladsl.Source
import akka.util.ByteString

import scala.concurrent.duration._

case class Image(id: String) {
  def updated = Image(s"Hello $id")
}

object Image {
  import scala.pickling.Defaults._
  import scala.pickling.binary._

  val delay = 1.milli
//  val delay = 1.second

  val stream = Source(delay, delay, ())
    .scan(1)((acc, _) => acc + 1)
    .map(x => Image(x.toString))

  val ten = stream.take(10)

  val single = Source.single(Image(""))
  val lazyEmpty = Source.lazyEmpty[Image]

  def toBytes(image: Image) = ByteString(image.pickle.value)
  def fromBytes(byteString: ByteString) = toBinaryPickle(byteString.toArray).unpickle[Image]
}
