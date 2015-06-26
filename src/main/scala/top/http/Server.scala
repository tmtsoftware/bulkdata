package top.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.{ActorMaterializer, Materializer}
import akka.stream.scaladsl.Sink
import top.common.Image

import scala.util.{Failure, Success}

class Server(address: String, port: Int)(implicit system: ActorSystem, mat: Materializer) {
  val connections = Http().bind(address, port)

  val requestHandler: HttpRequest => HttpResponse = {
    case HttpRequest(HttpMethods.GET, Uri.Path("/images"), _, _, _) =>
      val chunked = HttpEntity.Chunked.fromData(ContentTypes.NoContentType, Image.ten.map(Image.toBytes))
      HttpResponse(entity = chunked)
    case _: HttpRequest                                             =>
      HttpResponse(StatusCodes.NotFound, entity = "error")
  }

  val runnableGraph = connections.to(Sink.foreach { connection =>
    println(s"Accepted new connection from: ${connection.remoteAddress}")
    connection.handleWithSyncHandler(requestHandler)
  })
}

object Server extends App {
  implicit val system = ActorSystem("TMT")
  import system.dispatcher
  implicit val mat = ActorMaterializer()

  val address = "localhost"
  val port    = 6001
  val server  = new Server(address, port)

  val eventualBinding = server.runnableGraph.run()

  eventualBinding.onComplete {
    case Success(b) => println(s"Server started, listening on: ${b.localAddress}")
    case Failure(e) => println(s"Server could not bind to $address:$port: ${e.getMessage}"); system.shutdown()
  }
}
