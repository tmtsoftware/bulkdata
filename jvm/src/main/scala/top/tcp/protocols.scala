package top.tcp

import akka.stream.scaladsl.Tcp.OutgoingConnection
import akka.stream.scaladsl._
import akka.util.ByteString
import top.common.Image

import scala.concurrent.Future

class ClientProtocol(images: Source[Image, Any]) {
  def transform(connectionFlow: Flow[ByteString, ByteString, Future[OutgoingConnection]]): Source[Image, Any] = images
    .log("sent") //next 3 lines are equivalent to .via(Image.stack.join(connectionFlow))
    .via(ImageProtocols.outbound)
    .via(connectionFlow)
    .via(ImageProtocols.inbound)
    .log("RECEIVED")
}

trait ServerProtocol {
  protected def transformation: Flow[Image, Image, Any]

  // same as Image.inbound.log("RECEIVED").via(transformation).via(Image.outbound)
  def connectionFlow = ImageProtocols.stack.reversed.join(
    Flow[Image].log("RECEIVED").via(transformation)
  )
}

object ServerProtocol {
  class Get(images: Source[Image, Any]) extends ServerProtocol {
    protected val transformation = Flow[Image].map(_ => images).flatten(FlattenStrategy.concat)
  }

  object Post extends ServerProtocol {
    protected val transformation = Flow[Image].filter(_ => false)
  }

  object Bidi extends ServerProtocol {
    protected val transformation = Flow[Image].map(_.updated)
  }
}
