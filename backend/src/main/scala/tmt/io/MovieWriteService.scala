package tmt.io

import java.io.File

import akka.stream.io.SynchronousFileSink
import akka.stream.scaladsl.Source
import akka.util.ByteString
import tmt.app.{AppSettings, ActorConfigs}

class MovieWriteService(actorConfigs: ActorConfigs, settings: AppSettings) {

  import actorConfigs._

  def copyMovie(name: String, byteArrays: Source[ByteString, Any]) = {
    val file = new File(s"${settings.moviesOutputDir}/$name")
    println(s"writing to $file")
    byteArrays.runWith(SynchronousFileSink(file)).map(_ => ())
  }
}
