package tmt.dsl

import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import tmt.bridge.SourceSimulator
import tmt.common._

class BoxService(implicit mat: Materializer) {
  def send = Source(() => Producer.boxes()).take(10).log("sending")
  def copy(boxes: Source[Box, Any]) = boxes.log("receiving").runWith(Sink.ignore)
  def transform(boxes: Source[Box, Any]) = boxes.log("receiving").map(_.updated).log("sending")
}
