package tmt.common

case class Box(id: String) {
  def updated = Box(s"Hello $id")
}

object Box {
  import boopickle.Default._
  implicit val pickler = generatePickler[Box]
}
