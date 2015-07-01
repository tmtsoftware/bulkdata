package top.dsl

import java.io.File

import akka.http.scaladsl.model.ws.BinaryMessage
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import top.common._

class FrameService(implicit mat: Materializer) {
  def send = frames.map(RealImageConversions.toByteString).map(BinaryMessage.Strict)

  def frames = {
    def files = new File("/Users/mushtaq/videos/frames").listFiles().iterator.take(100)
    Source(() => files).map(RealImageConversions.fromFile)
  }
}
