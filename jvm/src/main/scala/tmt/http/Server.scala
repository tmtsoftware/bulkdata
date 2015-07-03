package tmt.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.{Materializer, ActorMaterializer}
import akka.stream.scaladsl.Sink

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class Server(address: String, port: Int, handler: HttpRequest => Future[HttpResponse])(implicit system: ActorSystem, mat: Materializer, executor: ExecutionContext) {

  val runnableGraph = {
    val connections = Http().bind(address, port)

    connections.to(Sink.foreach { connection =>
      println(s"Accepted new connection from: ${connection.remoteAddress}")
      connection.handleWithAsyncHandler(handler)
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

  val server  = new Server("localhost", 6001, new Handler().requestHandler)

  server.run()
}
