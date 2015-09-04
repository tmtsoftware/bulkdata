package tmt.common

case class Image(name: String, bytes: Array[Byte]) {
  def size = bytes.length
}
