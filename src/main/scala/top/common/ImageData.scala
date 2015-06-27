package top.common

import akka.stream.scaladsl.Source

object ImageData {

  val delay = Config.delay

  val stream = Source(delay, delay, ())
    .scan(1)((acc, _) => acc + 1)
    .map(x => Image(x.toString))

  val ten = stream.take(10)

  val single = Source.single(Image(""))
  val lazyEmpty = Source.lazyEmpty[Image]
}
