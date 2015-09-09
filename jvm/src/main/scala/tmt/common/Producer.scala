package tmt.common

import java.io.File

import tmt.common.models.Box

class Producer(appSettings: AppSettings) {
  def numbers() = Iterator.from(1)
  def boxes() = numbers().map(x => Box(x.toString))
  def list(dir: String) = new File(dir).listFiles().sortBy(_.getName)

  def files(dir: String): Iterator[File] = {
    val files = list(dir)
    Iterator.continually(files).flatten
  }.take(appSettings.maxTransferFiles)
}
