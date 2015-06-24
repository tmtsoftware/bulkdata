package top.protocols.serversent

import akka.actor.ActorSystem
import akka.stream.scaladsl._
import akka.util.ByteString
import top.protocols.{ServerProtocol, Image, Server}

object ServerSentServer extends App {

  // Use parameters `host port` to start server listening on host:port.
  val Array(host, port) = args

  implicit val system = ActorSystem("SeverSent-Server")
  new Server(host, port.toInt, ServerSentServerProtocol).run()
}

object ServerSentServerProtocol extends ServerProtocol {
  override def connectionFlow = transformation.via(Image.outbound)

  private val transformation = Flow() { implicit b =>
    import FlowGraph.Implicits._

    val mainFlow = b.add(Flow[Image])
    val ignoredFlow = b.add(Flow[ByteString].log("RECEIVED"))
    val ignore = b.add(Sink.ignore)

    Image.stream ~> mainFlow.inlet
    ignoredFlow.outlet ~> ignore

    (ignoredFlow.inlet, mainFlow.outlet)
  }
}
