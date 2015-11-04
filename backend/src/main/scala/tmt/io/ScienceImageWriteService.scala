package tmt.io

import java.io.File

import akka.stream.io.SynchronousFileSink
import akka.stream.scaladsl.Source
import akka.util.ByteString
import tmt.app.{AppSettings, ActorConfigs}

class ScienceImageWriteService(actorConfigs: ActorConfigs, settings: AppSettings) {

  import actorConfigs._

  def copy(name: String, byteArrays: Source[ByteString, Any]) = {
    val file = new File(s"${settings.scienceImagesOutputDir}/$name")
    println(s"writing to $file")
    byteArrays.runWith(SynchronousFileSink(file)).map(_ => ())
  }
}
