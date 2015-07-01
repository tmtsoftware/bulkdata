package top.common

case class Box(id: String) {
  def updated = Box(s"Hello $id")
}

case class Image(
  name: String,
  mimeType: String,
  width: Int,
  height: Int,
  bytes: Array[Byte]
) {
  def aspect = width.toDouble / height
  def scaledHeight(w: Int) = (w/aspect).toInt
}
