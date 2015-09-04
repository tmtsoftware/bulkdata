package tmt.library

import akka.stream.scaladsl._
import tmt.common.Sources

import scala.concurrent.duration.FiniteDuration

object FlowExtensions {

  implicit class RichFlow[In, Out, Mat](val flow: Flow[In, Out, Mat]) extends AnyVal {

    def zip[Out2, Mat2](other: Source[Out2, Mat2]) = Flow(flow) { implicit b => flw =>
      import FlowGraph.Implicits._

      val zipper = b.add(Zip[Out, Out2]())

      flw.outlet ~> zipper.in0
      other ~> zipper.in1

      (flw.inlet, zipper.out)
    }

    def zipWithIndex = zip(Source(() => Iterator.from(0)))

    def throttle(duration: FiniteDuration) = flow.zip(Sources.ticks(duration)).map(_._1)
  }

}
