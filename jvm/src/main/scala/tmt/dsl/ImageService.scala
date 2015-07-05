package tmt.dsl

import java.io.File
import java.nio.file.Files

import akka.actor.ActorSystem
import akka.http.scaladsl.model.ws.BinaryMessage
import akka.stream.io.SynchronousFileSource
import akka.stream.scaladsl.Source
import tmt.common._

import scala.concurrent.Future

class ImageService(implicit system: ActorSystem) {

  val fileIoDispatcher = system.dispatchers.lookup("akka.stream.default-file-io-dispatcher")

  def files = Source(() => Producer.files)

  def sendBytes = files.mapAsync(1)(readFile)

  def sendMessages = files.map(SynchronousFileSource(_)).map(BinaryMessage.apply)

  def readFile(file: File) = Future(Files.readAllBytes(file.toPath))(fileIoDispatcher)
}
