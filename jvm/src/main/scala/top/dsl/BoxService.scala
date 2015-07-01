package top.dsl

import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import top.common._

class BoxService(implicit mat: Materializer) {
  def send = SourceFactory.from(Producer.boxes()).take(10).log("sending")
  def copy(boxes: Source[Box, Any]) = boxes.log("receiving").runWith(Sink.ignore)
  def transform(boxes: Source[Box, Any]) = boxes.log("receiving").map(_.updated).log("sending")
}
