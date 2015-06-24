package top.protocols.bidi

import akka.actor.ActorSystem
import akka.stream.scaladsl._
import top.protocols.{Image, Server, ServerProtocol}

object BidiServer extends App {

  // Use parameters `host port` to start server listening on host:port.
  val Array(host, port) = args

  implicit val system = ActorSystem("Bidi-Server")
  new Server(host, port.toInt, BidiServerProtocol).run()
}

object BidiServerProtocol extends ServerProtocol {
  override def connectionFlow = Image.stack.reversed.join(transformation)
  private val transformation = Flow[Image].log("received").map(_.updated)
}
