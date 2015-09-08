package tmt.common

import java.io.File

import tmt.common.models.Box

object Producer {
  def numbers() = Iterator.from(1)
  def boxes() = numbers().map(x => Box(x.toString))
  def list(dir: String) = new File(dir).listFiles().sortBy(_.getName)

  def filesOnce(dir: String) = list(dir).iterator

  def filesInLoop(dir: String): Iterator[File] = {
    val files = list(dir)
    Iterator.continually(files).flatten
  }
}
