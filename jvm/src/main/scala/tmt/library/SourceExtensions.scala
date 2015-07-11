package tmt.library

import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source, Zip, FlowGraph}

object SourceExtensions {

  implicit class RichSource[Out, Mat](val source: Source[Out, Mat]) extends AnyVal {

    def zip[Out2, Mat2](other: Source[Out2, Mat2]) = Source(source) { implicit b => src =>
      import FlowGraph.Implicits._

      val zipper = b.add(Zip[Out, Out2]())

      src ~> zipper.in0
      other ~> zipper.in1

      zipper.out
    }

    def multicast(implicit mat: Materializer) = Source(source.runWith(Sink.fanoutPublisher(4, 16)))
  }

}
