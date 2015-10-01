package tmt.io

import java.io.File
import java.nio.file.Files

import akka.stream.io.SynchronousFileSink
import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString
import tmt.app.{ActorConfigs, AppSettings}
import tmt.library.SourceExtensions.RichSource
import tmt.shared.models.Image

import scala.concurrent.Future

class ImageWriteService(actorConfigs: ActorConfigs, settings: AppSettings) {

  import actorConfigs._

  private val parallelism = 1

  def copyBytes(byteArrays: Source[ByteString, Any]) = byteArrays.zipWithIndex
    .map { case (data, index) => data -> f"out-image-$index%05d.jpg" }
    .mapAsync(parallelism) { case (data, file) => copyFile(file, data.toArray) }
    .runWith(Sink.ignore)
  
  def copyImages(images: Source[Image, Any]) = images.mapAsync(parallelism)(copyImage).runWith(Sink.ignore)

  def copyImage(image: Image) = copyFile(image.name, image.bytes).map(_ => ())

  private def copyFile(name: String, data: Array[Byte]) = Future {
    val file = new File(s"${settings.framesOutputDir}/$name")
    println(s"writing to $file")
    Files.write(file.toPath, data)
  }(settings.fileIoDispatcher)
}
