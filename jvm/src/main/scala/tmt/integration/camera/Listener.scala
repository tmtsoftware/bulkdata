package tmt.integration.camera

trait Listener[T] {
  def onEvent(event: T): Unit
  def onError(ex: Throwable): Unit
  def onComplete(): Unit
}
