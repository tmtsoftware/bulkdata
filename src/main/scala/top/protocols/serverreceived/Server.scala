package top.protocols.serverreceived

import akka.actor.ActorSystem
import akka.stream.scaladsl._
import akka.util.ByteString
import top.protocols.{Image, Server, ServerProtocol}

object ServerReceivedServer extends App {

  // Use parameters `host port` to start server listening on host:port.
  val Array(host, port) = args

  implicit val system = ActorSystem("SeverReceived-Server")
  new Server(host, port.toInt, ServerReceivedServerProtocol).run()
}

object ServerReceivedServerProtocol extends ServerProtocol {
  override def connectionFlow = transformation.via(Image.outbound)

  val emptySource = Source.lazyEmpty[Image]
  val flow = Flow[ByteString].via(Image.inbound).log("RECEIVED")
  val sink = flow.toMat(Sink.foreach(_ => ()))(Keep.right)

  private val transformation = Flow(emptySource, sink)((_, _)) { implicit b => (src, snk) =>
    import FlowGraph.Implicits._
    val ignore = b.add(Sink.ignore)

    val fulfilledPromise = b.materializedValue.map { case (p, f) =>
      p.completeWith(f)
    }
    
    fulfilledPromise.outlet ~> ignore.inlet
    
    (snk.inlet, src.outlet)
  }
}
