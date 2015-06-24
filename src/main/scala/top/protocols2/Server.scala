package top.protocols2

import akka.actor.ActorSystem
import akka.stream.ActorFlowMaterializer
import akka.stream.scaladsl._
import akka.util.ByteString

import scala.concurrent.duration._
import scala.util.{Failure, Success}

object Server extends App {

  /**
   * Use parameters `host port` to start server listening on host:port.
   */
  implicit val system = ActorSystem("Server")
  val Array(host, port) = args
  server(host, port.toInt)

  def server(address: String, port: Int)(implicit system: ActorSystem): Unit = {
    import system.dispatcher
    implicit val materializer = ActorFlowMaterializer()

    val images = Source(1.second, 1.second, ()).scan(1)((acc, _) => acc + 1).map(Image.fromInt)

    val protocol = Flow() { implicit b =>
      import FlowGraph.Implicits._

      val mainFlow = b.add(Flow[Image])
      val ignoredFlow = b.add(Flow[ByteString].log("RECEIVED"))
      val ignore = b.add(Sink.ignore)

      images ~> mainFlow.inlet
      ignoredFlow.outlet ~> ignore

      (ignoredFlow.inlet, mainFlow.outlet)
    }

    val connectionFlow = protocol.via(Image.outbound)

    val handler = Sink.foreach[Tcp.IncomingConnection] { conn =>
      println("Client connected from: " + conn.remoteAddress)
      conn handleWith connectionFlow
    }

    val connections = Tcp().bind(address, port)
    val binding = connections.to(handler).run()

    binding.onComplete {
      case Success(b) =>
        println("Server started, listening on: " + b.localAddress)
      case Failure(e) =>
        println(s"Server could not bind to $address:$port: ${e.getMessage}")
        system.shutdown()
    }
  }
}
