package top.protocols2

import java.nio.ByteOrder

import akka.stream.BidiShape
import akka.stream.scaladsl.{BidiFlow, Flow}
import akka.util.ByteString

object Framing {
  implicit val order: ByteOrder = ByteOrder.LITTLE_ENDIAN

  val value = BidiFlow() { b =>
    val outbound = b.add(Flow[ByteString].map(addLengthHeader))
    val inbound = b.add(Flow[ByteString].transform(() => new FrameParser))
    BidiShape(outbound, inbound)
  }

  def addLengthHeader(bytes: ByteString) = {
    val len = bytes.length
    ByteString.newBuilder.putInt(len).append(bytes).result()
  }
}
