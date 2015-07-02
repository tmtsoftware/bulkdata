package top

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import org.scalajs.dom._

@JSExport
object Game {
  @JSExport
  def main() = {
    println("hello!")

    var i = 0

    onmessage = {
      case e: MessageEvent => println(s"received ${e.data}")
      case sender: Worker => println(s"received $sender")
      case x               => println(s"received $x")
    }: js.Any => Any



    def timedCount(): Unit = {
      i = i + 1
      println(s"sending $i")
      self.asInstanceOf[Worker].postMessage(i)
      setTimeout(() => timedCount(), 500)
    }

    println(s"starting counter")
    timedCount()
  }
}
