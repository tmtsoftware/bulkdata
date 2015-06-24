package top.protocols.serversent

import akka.actor.ActorSystem
import akka.stream.scaladsl.Tcp.OutgoingConnection
import akka.stream.scaladsl._
import akka.util.ByteString
import top.protocols.{Client, ClientProtocol, Image}

import scala.concurrent.Future

object ServerSentClient extends App {

  /**
   * Use parameters `host port` to connect to server at host:port.
   */
  val Array(host, port) = args

  implicit val system = ActorSystem("ServerSent-Client")
  new Client(host, port.toInt, ServerSentClientProtocol).run()
}

object ServerSentClientProtocol extends ClientProtocol {
  override def imageSource(connectionFlow: Flow[ByteString, ByteString, Future[OutgoingConnection]]) = {
    val transformations = connectionFlow.via(Image.inbound)
    Source.single(ByteString(77))
      .log("sent")
      .via(transformations)
      .log("RECEIVED")
  }
}
