package top.common

object Producer {
  def numbers() = Iterator.from(1)
  def images() = numbers().map(x => Image(x.toString))
}
