package tmt.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.{Materializer, ActorMaterializer}
import akka.stream.scaladsl.{Flow, Sink}
import tmt.common.{Config, Server, Types}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class HttpServer(val interface: String, val port: Int) {
  implicit val system = ActorSystem("TMT")
  implicit val mat    = ActorMaterializer()
  implicit val ec     = system.dispatcher

  val connectionFlow = Flow[HttpRequest].mapAsync(1)(new Handler().requestHandler)
  val server  = new Server(interface, port, connectionFlow)
}

object HttpServer extends HttpServer(Config.interface, Config.port) with App {
  server.run()
}
