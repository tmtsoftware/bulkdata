package top.tcp

import akka.stream.scaladsl.Tcp.OutgoingConnection
import akka.stream.scaladsl._
import akka.util.ByteString
import top.common.Box

import scala.concurrent.Future

class ClientProtocol(images: Source[Box, Any]) {
  def transform(connectionFlow: Flow[ByteString, ByteString, Future[OutgoingConnection]]): Source[Box, Any] = images
    .log("sent") //next 3 lines are equivalent to .via(Box.stack.join(connectionFlow))
    .via(BoxProtocols.outbound)
    .via(connectionFlow)
    .via(BoxProtocols.inbound)
    .log("RECEIVED")
}

trait ServerProtocol {
  protected def transformation: Flow[Box, Box, Any]

  // same as Box.inbound.log("RECEIVED").via(transformation).via(Box.outbound)
  def connectionFlow = BoxProtocols.stack.reversed.join(
    Flow[Box].log("RECEIVED").via(transformation)
  )
}

object ServerProtocol {
  class Get(boxes: Source[Box, Any]) extends ServerProtocol {
    protected val transformation = Flow[Box].map(_ => boxes).flatten(FlattenStrategy.concat)
  }

  object Post extends ServerProtocol {
    protected val transformation = Flow[Box].filter(_ => false)
  }

  object Bidi extends ServerProtocol {
    protected val transformation = Flow[Box].map(_.updated)
  }
}
