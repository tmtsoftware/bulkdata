package tmt.common

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.Materializer
import akka.stream.scaladsl.Sink

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class Server(address: String, port: Int, connectionFlow: Types.ConnectionFlow)(implicit system: ActorSystem, mat: Materializer, executor: ExecutionContext) {

  val runnableGraph = {
    val connections = Http().bind(address, port)

    connections.to(Sink.foreach { connection =>
      println(s"Accepted new connection from: ${connection.remoteAddress}")
      connection.handleWith(connectionFlow)
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
