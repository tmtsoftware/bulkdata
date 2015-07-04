package tmt.dsl

import java.nio.file.Files

import akka.http.scaladsl.model.ws.BinaryMessage
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import akka.stream.Materializer
import akka.stream.io.SynchronousFileSource
import akka.stream.scaladsl.Source
import akka.util.ByteString
import tmt.common._

class ImageService(implicit mat: Materializer) {

  def files = Source(() => Producer.files)

  def sendResponses = files.map(SynchronousFileSource(_))
    .map(HttpEntity.Chunked.fromData(ContentTypes.NoContentType, _))
    .map(chunked => HttpResponse(entity = chunked))
  
  def sendBytes = files.map(file => Files.readAllBytes(file.toPath))

  def sendStrictMessages = sendBytes.map(ByteString.apply)
    .map(BinaryMessage.Strict)

  def sendMessages = files.map(SynchronousFileSource(_))
    .map(BinaryMessage.apply)

}
