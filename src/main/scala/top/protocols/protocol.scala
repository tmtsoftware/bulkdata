package top.protocols

import akka.stream.scaladsl.Tcp.OutgoingConnection
import akka.stream.scaladsl.{Flow, Source}
import akka.util.ByteString

import scala.concurrent.Future

trait ServerProtocol {
  def connectionFlow: Flow[ByteString, ByteString, Unit]
}

trait ClientProtocol {
  def imageSource(connectionFlow: Flow[ByteString, ByteString, Future[OutgoingConnection]]): Source[Image, Any]
}
