package top.tcp

import akka.stream.scaladsl.Tcp.OutgoingConnection
import akka.stream.scaladsl._
import akka.util.ByteString

import scala.concurrent.Future

class ClientProtocol(images: Source[Image, Any]) {
  def transform(connectionFlow: Flow[ByteString, ByteString, Future[OutgoingConnection]]): Source[Image, Any] = images
    .log("sent") //next 3 lines are equivalent to .via(Image.stack.join(connectionFlow))
    .via(Image.outbound)
    .via(connectionFlow)
    .via(Image.inbound)
    .log("RECEIVED")
}

trait ServerProtocol {
  protected def transformation: Flow[Image, Image, Any]

  // same as Image.inbound.via(transformation).via(Image.outbound)
  def connectionFlow = Image.stack.reversed.join(transformation)
}

object ServerProtocol {
  class Get(images: Source[Image, Any]) extends ServerProtocol {

    protected val transformation = Flow() { implicit b =>
      import FlowGraph.Implicits._

      val mainFlow = b.add(Flow[Image])
      val ignoredFlow = b.add(Flow[Image].log("RECEIVED"))
      val ignore = b.add(Sink.ignore)

      images ~> mainFlow.inlet
      ignoredFlow.outlet ~> ignore

      (ignoredFlow.inlet, mainFlow.outlet)
    }
  }

  object Post extends ServerProtocol {

    val flow = Flow[Image].log("RECEIVED")
    val sink = flow.toMat(Sink.foreach(_ => ()))(Keep.right)

    protected val transformation = Flow(Image.lazyEmpty, sink)((_, _)) { implicit b => (src, snk) =>
      import FlowGraph.Implicits._

      val fulfilledPromise = b.materializedValue.map { case (p, f) =>
        p.completeWith(f)
      }

      val ignore = b.add(Sink.ignore)

      fulfilledPromise.outlet ~> ignore.inlet

      (snk.inlet, src.outlet)
    }
  }

  object Bidi extends ServerProtocol {
    protected val transformation = Flow[Image].log("RECEIVED").map(_.updated)
  }
}
