package top.protocols

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Tcp}

import scala.util.{Failure, Success}

class Server(address: String, port: Int, serverProtocol: ServerProtocol)(implicit system: ActorSystem) {
  import system.dispatcher
  implicit val materializer = ActorMaterializer()

  def run(): Unit = binding.onComplete {
    case Success(b) =>
      println("Server started, listening on: " + b.localAddress)
    case Failure(e) =>
      println(s"Server could not bind to $address:$port: ${e.getMessage}")
      system.shutdown()
  }

  val handler = Sink.foreach[Tcp.IncomingConnection] { conn =>
    println("Client connected from: " + conn.remoteAddress)
    conn handleWith serverProtocol.connectionFlow
  }

  val connections = Tcp().bind(address, port)

  val binding = connections.to(handler).run()
}
