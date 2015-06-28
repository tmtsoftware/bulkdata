package top.dsl

import akka.http.scaladsl.model.ws.{BinaryMessage, TextMessage, Message}
import akka.stream.Materializer
import akka.stream.scaladsl.{FlattenStrategy, Flow, Sink, Source}
import top.common._

class ImageService(implicit mat: Materializer) {
  //  val images = ImageData.stream
  def images = SourceFactory.from(Producer.images())
  def read = images.take(10).log("sending")
  def copy(images: Source[Image, Any]) = images.log("receiving").runWith(Sink.ignore)
  def transform(images: Source[Image, Any]) = images.log("receiving").map(_.updated).log("sending")

  val join = Flow[Message].collect {
    case TextMessage.Strict("join") => read.map(Image.toBytes).map(BinaryMessage.Strict)
  }.flatten(FlattenStrategy.concat)

}
