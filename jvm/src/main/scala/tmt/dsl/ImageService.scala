package tmt.dsl

import akka.http.scaladsl.model.ws.BinaryMessage
import akka.stream.Materializer
import akka.stream.io.SynchronousFileSource
import akka.stream.scaladsl.Source
import tmt.common._

class ImageService(implicit mat: Materializer) {

  def send = Source(() => Producer.files)
    .map(file => SynchronousFileSource(file))
    .map(BinaryMessage.apply)

}
