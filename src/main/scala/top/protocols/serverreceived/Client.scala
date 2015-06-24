package top.protocols.serverreceived

import akka.actor.ActorSystem
import akka.stream.scaladsl.Tcp.OutgoingConnection
import akka.stream.scaladsl._
import akka.util.ByteString
import top.protocols.{Client, ClientProtocol, Image}

import scala.concurrent.Future

object ServerReceivedClient extends App {

  /**
   * Use parameters `host port` to connect to server at host:port.
   */
  val Array(host, port) = args

  implicit val system = ActorSystem("ServerReceived-Client")
  new Client(host, port.toInt, ServerReceivedClientProtocol).run()
}

object ServerReceivedClientProtocol extends ClientProtocol {
  override def imageSource(connectionFlow: Flow[ByteString, ByteString, Future[OutgoingConnection]]) = Image.stream
    .take(10) //made the stream finite to demo proper closing of connection after stream gets over
    .log("sent")
    .via(Image.outbound)
    .via(connectionFlow)
    .via(Image.inbound)
    .log("RECEIVED")
}
