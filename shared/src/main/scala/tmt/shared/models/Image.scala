package tmt.shared.models

import boopickle.Default._

case class Image(name: String, bytes: Array[Byte], timestamp: Long) {
  def size = bytes.length
}

object Image {
  implicit val pickler = generatePickler[Image]
}
