package tmt.common
import boopickle.Default._

case class Box(id: String) {
  def updated = Box(s"Hello $id")
}

object Box {
  implicit val pickler = generatePickler[Box]
}
