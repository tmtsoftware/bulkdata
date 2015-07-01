package top.dsl

import java.io.File

import akka.http.scaladsl.model.ws.BinaryMessage
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import top.common._

class ImageService(implicit mat: Materializer) {
  def send = images.map(ImageConversions.toByteString).map(BinaryMessage.Strict)

  def images = {
    def files = new File("/Users/mushtaq/videos/frames").listFiles().iterator
    Source(() => files).map(ImageConversions.fromFile)
  }
}
