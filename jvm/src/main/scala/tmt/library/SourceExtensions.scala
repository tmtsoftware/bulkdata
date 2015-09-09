package tmt.library

import akka.stream.scaladsl._
import akka.stream.{Materializer, OverflowStrategy}
import tmt.common.Sources
import tmt.integration.bridge.Connector

import scala.concurrent.duration.FiniteDuration

object SourceExtensions {

  implicit class RichSource[Out, Mat](val source: Source[Out, Mat]) extends AnyVal {

    def zip[Out2, Mat2](other: Source[Out2, Mat2]) = Source(source) { implicit b => src =>
      import FlowGraph.Implicits._

      val zipper = b.add(Zip[Out, Out2]())

      src ~> zipper.in0
      other ~> zipper.in1

      zipper.out
    }

    def zipWithIndex = zip(Source(() => Iterator.from(0)))

    def throttle(duration: FiniteDuration) = source.zip(Sources.ticks(duration)).map(_._1)

    def removeBackPressure = source.buffer(1, OverflowStrategy.dropHead)
    def hotMulticast(implicit mat: Materializer) = Source(source.removeBackPressure.runWith(Sink.fanoutPublisher(2, 2)))
    def hotUnicast(implicit mat: Materializer) = Source(source.removeBackPressure.runWith(Sink.publisher))
    def multicast(implicit mat: Materializer) = Source(source.runWith(Sink.fanoutPublisher(2, 2)))
  }

  def merge[Out, Mat](sources: Seq[Source[Out, Mat]]) =
    if(sources.isEmpty)
      Source.empty[Out]
    else {
      Source() { implicit b =>
        import FlowGraph.Implicits._

        val merge = b.add(Merge[Out](sources.length))

        sources.zipWithIndex.foreach { case (source, index) => source ~> merge.in(index) }

        merge.out
      }
    }

}
