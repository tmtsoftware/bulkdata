package tmt.media.server

import java.io.File
import java.nio.file.Files

import akka.http.scaladsl.model.ws.BinaryMessage
import akka.stream.io.SynchronousFileSource
import akka.stream.scaladsl.Source
import akka.util.ByteString
import tmt.common._

import scala.concurrent.Future

class MediaReadService(actorConfigs: ActorConfigs, settings: AppSettings) {

  private val parallelism = 1

  private def files = Source(() => Producer.files(settings.framesInputDir))

  def sendBytes = files.mapAsync(parallelism)(readFile).map(ByteString.apply)
  def sendImages = files.mapAsync(parallelism)(readImage)
  def sendMessages = files.map(SynchronousFileSource(_)).map(BinaryMessage.apply)
  def sendMovie(name: String) = {
    val file = new File(s"${settings.moviesInputDir}/$name")
    println(s"reading from $file")
    SynchronousFileSource(file)
  }
  def listMovies = Source(() => new File(settings.moviesInputDir).list().iterator)

  private def readFile(file: File) = Future(Files.readAllBytes(file.toPath))(settings.fileIoDispatcher)
  private def readImage(file: File) = readFile(file).map(data => Image(file.getName, data))(settings.fileIoDispatcher)
}
