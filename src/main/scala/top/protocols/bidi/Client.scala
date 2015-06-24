package top.protocols.bidi

import akka.actor.ActorSystem
import akka.stream.scaladsl.Tcp.OutgoingConnection
import akka.stream.scaladsl._
import akka.util.ByteString
import top.protocols.{Client, Image, ClientProtocol}

import scala.concurrent.Future

object BidiClient extends App {

  /**
   * Use parameters `host port` to connect to server at host:port.
   */
  val Array(host, port) = args

  implicit val system = ActorSystem("Client")
  new Client(host, port.toInt, BidiClientProtocol).run()
}

object BidiClientProtocol extends ClientProtocol {
  override def imageSource(connectionFlow: Flow[ByteString, ByteString, Future[OutgoingConnection]]) = {
    val transformations = Image.stack.join(connectionFlow)
    Image.stream
      .log("sent")
      .via(transformations)
      .log("RECEIVED")
  }
}
