package tmt.boxes.http

import akka.stream.scaladsl.{Sink, Source}
import tmt.common._
import tmt.common.models.Box

class BoxService(actorConfigs: ActorConfigs, producer: Producer) {
  import actorConfigs._

  def send = Source(() => producer.boxes()).take(10).log("sending")
  def copy(boxes: Source[Box, Any]) = boxes.log("receiving").runWith(Sink.ignore)
  def transform(boxes: Source[Box, Any]) = boxes.log("receiving").map(_.updated).log("sending")
}
