package tmt.io

import java.io.File

import akka.stream.io.SynchronousFileSource
import akka.stream.scaladsl.Source
import tmt.app.AppSettings

class ScienceImageReadService(settings: AppSettings) {
  def send(name: String) = {
    val file = new File(s"${settings.moviesInputDir}/$name")
    println(s"reading from $file")
    SynchronousFileSource(file)
  }

  def movies = new File(settings.moviesInputDir).list().toList

  def listMovies = Source(() => movies.iterator)
}
