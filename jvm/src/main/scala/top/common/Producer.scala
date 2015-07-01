package top.common

object Producer {
  def numbers() = Iterator.from(1)
  def boxes() = numbers().map(x => Box(x.toString))
}
