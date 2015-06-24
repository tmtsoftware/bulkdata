package top.protocols

import java.nio.ByteOrder

import akka.stream.BidiShape
import akka.stream.scaladsl.{BidiFlow, Flow}
import akka.util.ByteString

object Framing {
  implicit val order: ByteOrder = ByteOrder.LITTLE_ENDIAN

  val frameParser = new FrameParser

  val bidi = BidiFlow() { b =>
    val outbound = b.add(Flow[ByteString].map(addLengthHeader))
    val inbound = b.add(Flow[ByteString].transform(() => frameParser))
    BidiShape(outbound, inbound)
  }

  def addLengthHeader(bytes: ByteString) = {
    val len = bytes.length
    ByteString.newBuilder.putInt(len).append(bytes).result()
  }
}
