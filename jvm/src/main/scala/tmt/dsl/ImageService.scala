package tmt.dsl

import akka.http.scaladsl.model.ws.BinaryMessage
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import tmt.common._

class ImageService(implicit mat: Materializer) {

  def send = Source(() => Producer.files)
    .map(ImageConversions.fromFile)
    .map(ImageConversions.toByteString)
    .map(BinaryMessage.Strict)

}
