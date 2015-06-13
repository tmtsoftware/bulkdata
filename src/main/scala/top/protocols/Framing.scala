package top.protocols

import java.nio.ByteOrder

import akka.stream.BidiShape
import akka.stream.scaladsl.{BidiFlow, Flow}
import akka.util.ByteString

object Framing {
  val value = BidiFlow() { b =>

    implicit val order = ByteOrder.LITTLE_ENDIAN

    def addLengthHeader(bytes: ByteString) = {
      val len = bytes.length
      ByteString.newBuilder.putInt(len).append(bytes).result()
    }

    val outbound = b.add(Flow[ByteString].map(addLengthHeader))
    val inbound = b.add(Flow[ByteString].transform(() => new FrameParser))
    BidiShape(outbound, inbound)
  }
}

