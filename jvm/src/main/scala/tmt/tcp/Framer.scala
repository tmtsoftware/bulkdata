package tmt.tcp

import java.nio.ByteOrder

import akka.stream.io.Framing
import akka.stream.scaladsl.{BidiFlow, Flow, Keep}
import akka.util.ByteString

object Framer {
  val order = ByteOrder.BIG_ENDIAN
  val MaximumMessageLength = 10000000

  val decoder = Framing.lengthField(4, 0, MaximumMessageLength + 4, order).map(_.drop(4))
  val encoder = Flow[ByteString].map(addLengthHeader)
  val codec = BidiFlow.wrap(encoder, decoder)(Keep.left)
  
  private def addLengthHeader(bytes: ByteString) = {
    val len = bytes.length
    ByteString.newBuilder.putInt(len)(order).append(bytes).result()
  }
}
