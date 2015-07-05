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

  val parallelism = 1

  def sendBytes = files.mapAsync(parallelism)(readFile)
  def sendImages = files.mapAsync(parallelism)(readImage)
  def sendMessages = files.map(SynchronousFileSource(_)).map(BinaryMessage.apply)

  def copyBytes(byteArrays: Source[Array[Byte], Any]) = byteArrays.zip(fileNamesToWrite)
    .mapAsync(parallelism) { case (data, file) => copyFile(file, data) }
    .runWith(Sink.ignore)

  def copyImages(images: Source[Image, Any]) = images.mapAsync(parallelism)(copyImage).runWith(Sink.ignore)

  private def readFile(file: File) = Future(Files.readAllBytes(file.toPath))(fileIoDispatcher)
  private def readImage(file: File) = readFile(file).map(data => Image(file.getName, data))(fileIoDispatcher)

  private def fileNamesToWrite = Source(() => Producer.numbers()).map(index => f"out-image-$index%05d.jpg")

  private def copyFile(name: String, data: Array[Byte]) = Future {
    val file = new File(f"${Config.outputDir}/$name")
    println(s"writing to $file")
    Files.write(file.toPath, data)
  }(fileIoDispatcher)

  private def copyImage(image: Image) = copyFile(image.name, image.bytes)
}
