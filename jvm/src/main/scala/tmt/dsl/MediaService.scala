package tmt.dsl

import java.io.File
import java.nio.file.Files

import akka.actor.ActorSystem
import akka.http.scaladsl.model.ws.BinaryMessage
import akka.stream.Materializer
import akka.stream.io.SynchronousFileSource
import akka.stream.scaladsl.{Sink, Source}
import tmt.common._
import tmt.library.SourceExtensions.RichSource

import scala.concurrent.Future

class MediaService(implicit system: ActorSystem, mat: Materializer) {

  val fileIoDispatcher = system.dispatchers.lookup("akka.stream.default-file-io-dispatcher")

  def files = Source(() => Producer.files)

  def sendBytes = files.mapAsync(1)(readFile)

  def sendMessages = files.map(SynchronousFileSource(_)).map(BinaryMessage.apply)

  def readFile(file: File) = Future(Files.readAllBytes(file.toPath))(fileIoDispatcher)
  
  def copy(byteArrays: Source[Array[Byte], Any]) = byteArrays.zip(filesToWrite)
    .mapAsync(1) { case (data, file) =>
      println(s"writing to $file")
      Future(Files.write(file.toPath, data))(fileIoDispatcher)
    }
    .runWith(Sink.ignore)

  def filesToWrite = Source(() => Producer.numbers()).map(makeFileName)  
  def makeFileName(index: Int) = new File(f"${Config.outputDir}/out-image-$index%05d.jpg")
}
