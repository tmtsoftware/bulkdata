package tmt.boxes.http

import akka.stream.scaladsl.Source
import tmt.common.SharedConfigs
import tmt.common.models.Box

object Boxes {

  val delay = SharedConfigs.delay

  val stream = Source(delay, delay, ())
    .scan(1)((acc, _) => acc + 1)
    .map(x => Box(x.toString))

  val ten = stream.take(10)

  val single = Source.single(Box(""))
  val lazyEmpty = Source.lazyEmpty[Box]
}
