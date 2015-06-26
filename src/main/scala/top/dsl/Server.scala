package top.dsl

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink

import scala.util.{Failure, Success}

class Server(address: String, port: Int, route: Route)(implicit system: ActorSystem) {
  implicit val mat = ActorMaterializer()
  import system.dispatcher

  val connections = Http().bind(address, port)

  val runnableGraph = connections.to(Sink.foreach { connection =>
    println(s"Accepted new connection from: ${connection.remoteAddress}")
    connection.handleWith(route)
  })
}

object Server extends App {
  implicit val system = ActorSystem("TMT")
  import system.dispatcher
  implicit val mat = ActorMaterializer()

  val address = "localhost"
  val port    = 6001
  val server  = new Server(address, port, new ImageRoute(new ImageService).route)

  val eventualBinding = server.runnableGraph.run()

  eventualBinding.onComplete {
    case Success(b) => println(s"Server started, listening on: ${b.localAddress}")
    case Failure(e) => println(s"Server could not bind to $address:$port: ${e.getMessage}"); system.shutdown()
  }
}
