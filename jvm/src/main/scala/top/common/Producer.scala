package top.common

import java.io.File

object Producer {
  def numbers() = Iterator.from(1)
  def boxes() = numbers().map(x => Box(x.toString))
  def files() = new File("/Users/mushtaq/videos/frames").listFiles().iterator
}
