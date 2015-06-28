package top.tcp

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Tcp}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class Server(address: String, port: Int, serverProtocol: ServerProtocol)(implicit system: ActorSystem, mat: Materializer, executor: ExecutionContext) {

  val runnableGraph = {
    val connections = Tcp().bind(address, port)

    connections.to(Sink.foreach { connection =>
      println(s"Accepted new connection from: ${connection.remoteAddress}")
      connection.handleWith(serverProtocol.connectionFlow)
    })
  }

  def run() = {
    val binding = runnableGraph.run()

    binding.onComplete {
      case Success(b) => println(s"Server started, listening on: ${b.localAddress}")
      case Failure(e) => println(s"Server could not bind to $address:$port: ${e.getMessage}"); system.shutdown()
    }
  }
}
