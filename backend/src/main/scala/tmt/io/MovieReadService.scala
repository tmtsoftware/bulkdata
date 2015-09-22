package tmt.io

import java.io.File

import akka.stream.io.SynchronousFileSource
import akka.stream.scaladsl.Source
import tmt.app.AppSettings

class MovieReadService(settings: AppSettings) {
  def sendMovie(name: String) = {
    val file = new File(s"${settings.moviesInputDir}/$name")
    println(s"reading from $file")
    SynchronousFileSource(file)
  }
  def listMovies = Source(() => new File(settings.moviesInputDir).list().iterator)
}
