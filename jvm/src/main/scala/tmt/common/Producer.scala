package tmt.common

import java.io.File

object Producer {
  def numbers() = Iterator.from(1)
  def boxes() = numbers().map(x => Box(x.toString))
  def files = new File(Config.framesDir).listFiles().sortBy(_.getName).iterator
}
