package top.common

import java.util.concurrent.Executors

import scala.util.control.NonFatal

class Simulator[T](producer: Iterator[T]) {
  private val singleThreadScheduler = Executors.newScheduledThreadPool(1)
  private val delay = Config.delay

  def subscribe(listener: Listener[T]) = singleThreadScheduler.scheduleAtFixedRate(
    runnable(listener),
    delay.length,
    delay.length,
    delay.unit
  )

  private def runnable(listener: Listener[T]): Runnable = new Runnable {
    def run() = try {
      if (producer.hasNext)
        listener.onEvent(producer.next())
      else
        listener.onComplete()
    } catch {
      case NonFatal(ex) => listener.onError(ex)
    }
  }
}
