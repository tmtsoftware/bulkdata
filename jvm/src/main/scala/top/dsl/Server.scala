package top.dsl

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.Sink
import akka.stream.{ActorMaterializer, Materializer}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class Server(address: String, port: Int, route: Route)(implicit system: ActorSystem, mat: Materializer, executor: ExecutionContext) {

  val runnableGraph = {
    val connections = Http().bind(address, port)

    connections.to(Sink.foreach { connection =>
      println(s"Accepted new connection from: ${connection.remoteAddress}")
      connection.handleWith(route)
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

object Server extends App {
  implicit val system = ActorSystem("TMT")
  implicit val mat = ActorMaterializer()
  import system.dispatcher

  val server  = new Server("localhost", 6001, new AppRoute(new BoxService, new ImageService).route)

  server.run()
}
