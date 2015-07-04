package tmt.dsl

import java.nio.file.Files

import akka.http.scaladsl.model.ws.BinaryMessage
import akka.stream.Materializer
import akka.stream.io.SynchronousFileSource
import akka.stream.scaladsl.{FlattenStrategy, Source}
import akka.util.ByteString
import tmt.common._

class ImageService(implicit mat: Materializer) {

  def sendBytes = Source(() => Producer.files)
    .map(file => Files.readAllBytes(file.toPath))

  def sendStrictMessages = sendBytes
    .map(ByteString.apply)
    .map(BinaryMessage.Strict)

  def sendMessages = Source(() => Producer.files)
    .map(file => SynchronousFileSource(file))
    .map(BinaryMessage.apply)

}
