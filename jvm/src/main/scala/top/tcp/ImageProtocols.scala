package top.tcp

import akka.stream.scaladsl.{BidiFlow, Flow}
import akka.util.ByteString
import top.common.Image

object ImageProtocols {
  val codec = BidiFlow(Image.toBytes _, Image.fromBytes _)
  val stack = codec.atop(Framer.codec)
  val outbound = Flow[Image].map(Image.toBytes).via(Framer.encoder)
  val inbound = Flow[ByteString].via(Framer.decoder).map(Image.fromBytes)
}
