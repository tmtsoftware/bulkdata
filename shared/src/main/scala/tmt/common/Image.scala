package tmt.common

case class Image(name: String, bytes: Array[Byte]) {
  def size = bytes.length
}

object Image {
  import boopickle.Default._
  implicit val pickler = generatePickler[Image]
}
