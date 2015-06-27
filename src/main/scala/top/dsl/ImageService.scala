package top.dsl

import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import top.common._

class ImageService(implicit mat: Materializer) {
  val images = SourceFactory.from(Producer.images())
  def read = images.take(10).log("sending")
  def copy(images: Source[Image, Any]) = images.log("receiving").runWith(Sink.ignore)
  def transform(images: Source[Image, Any]) = images.log("receiving").map(_.updated).log("sending")
}
