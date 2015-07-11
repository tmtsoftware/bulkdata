package tmt.boxes.http

import akka.stream.scaladsl.{Sink, Source}
import tmt.common._

class BoxService(actorConfigs: ActorConfigs) {
  import actorConfigs._

  def send = Source(() => Producer.boxes()).take(10).log("sending")
  def copy(boxes: Source[Box, Any]) = boxes.log("receiving").runWith(Sink.ignore)
  def transform(boxes: Source[Box, Any]) = boxes.log("receiving").map(_.updated).log("sending")
}
