package tmt.common

case class Box(id: String) {
  def updated = Box(s"Hello $id")
}
