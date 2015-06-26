package top.dsl

import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import top.common.Image

class ImageService(implicit mat: Materializer) {
  def read = Image.ten.log("sending")
  def copy(images: Source[Image, Any]) = images.log("receiving").runWith(Sink.ignore)
  def transform(images: Source[Image, Any]) = images.log("receiving").map(_.updated).log("sending")
}
