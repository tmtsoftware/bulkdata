package tmt.common

import java.net.InetSocketAddress

import akka.http.scaladsl.Http
import akka.stream.scaladsl.Sink

import scala.util.{Failure, Success}

class Server(address: InetSocketAddress, connectionFlow: Types.ConnectionFlow)(implicit actorConfigs: ActorConfigs) {
  import actorConfigs._

  private val runnableGraph = {
    val connections = Http().bind(address.getHostName, address.getPort)

    connections.to(Sink.foreach { connection =>
      println(s"Accepted new connection from: ${connection.remoteAddress}")
      connection.handleWith(connectionFlow)
    })
  }

  def run() = {
    val binding = runnableGraph.run()

    binding.onComplete {
      case Success(b) => println(s"Server started, listening on: ${b.localAddress}")
      case Failure(e) => println(s"Server could not bind to $address due to: ${e.getMessage}"); system.shutdown()
    }

    binding
  }
}
