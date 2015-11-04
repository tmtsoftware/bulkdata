package tmt.io

import java.io.File

import akka.stream.io.SynchronousFileSource
import akka.stream.scaladsl.Source
import tmt.app.AppSettings

class ScienceImageReadService(settings: AppSettings) {
  def send(name: String) = {
    val file = new File(s"${settings.scienceImagesInputDir}/$name")
    println(s"reading from $file")
    SynchronousFileSource(file)
  }

  def scienceImages = new File(settings.scienceImagesInputDir).list().toList

  def listScienceImages = Source(() => scienceImages.iterator)
}
