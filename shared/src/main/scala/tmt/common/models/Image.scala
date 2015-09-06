package tmt.common.models

import boopickle.Default._

case class Image(name: String, bytes: Array[Byte]) {
  def size = bytes.length
}

object Image {
  implicit val pickler = generatePickler[Image]
}
