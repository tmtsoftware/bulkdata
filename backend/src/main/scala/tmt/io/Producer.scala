package tmt.io

import java.io.File

import tmt.app.AppSettings

class Producer(appSettings: AppSettings) {
  def numbers() = Iterator.from(1)
  def list(dir: String) = new File(dir).listFiles().sortBy(_.getName)

  def files(dir: String): Iterator[File] = {
    val files = list(dir)
    Iterator.continually(files).flatten
  }.take(appSettings.maxTransferFiles)
}
