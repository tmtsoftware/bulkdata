package top.tcp

import akka.stream.scaladsl.{BidiFlow, Flow}
import akka.util.ByteString
import top.common.{ImageConversions, Image}

object ImageProtocols {
  val codec = BidiFlow(ImageConversions.toBytes _, ImageConversions.fromBytes _)
  val stack = codec.atop(Framer.codec)
  val outbound = Flow[Image].map(ImageConversions.toBytes).via(Framer.encoder)
  val inbound = Flow[ByteString].via(Framer.decoder).map(ImageConversions.fromBytes)
}
