package top.protocols

import akka.actor.ActorSystem
import akka.stream.ActorFlowMaterializer
import akka.stream.scaladsl._

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

    val transformation = Flow[Image].log("received").map(_.updated)

    val connectionFlow = Image.stack.reversed.join(transformation)

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
