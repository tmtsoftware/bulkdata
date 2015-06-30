package top.dsl

import java.io.File

import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage}
import akka.stream.Materializer
import akka.stream.scaladsl.{FlattenStrategy, Flow, Sink, Source}
import top.common._

class ImageService(implicit mat: Materializer) {
  def images = SourceFactory.from(Producer.images()).take(10).log("sending")
  def copy(images: Source[Image, Any]) = images.log("receiving").runWith(Sink.ignore)
  def transform(images: Source[Image, Any]) = images.log("receiving").map(_.updated).log("sending")

  def sendRealImages = Flow[Message].collect {
    case TextMessage.Strict("join") => realImages.map(RealImageConversions.toByteString).map(BinaryMessage.Strict)
  }.flatten(FlattenStrategy.concat)

  def realImages = {
    def files = new File("/Users/mushtaq/videos/frames").listFiles().iterator
    Source(() => files).map(RealImageConversions.fromFile)
  }
}
