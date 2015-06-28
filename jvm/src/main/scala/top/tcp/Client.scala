package top.tcp

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Tcp}

import scala.util.{Failure, Success}

class Client(address: String, port: Int, clientProtocol: ClientProtocol)(implicit system: ActorSystem, mat: Materializer) {
  def run(): Unit = imagesFromServer.runWith(handler)

  val imagesFromServer = {
    val connectionFlow = Tcp().outgoingConnection(address, port)
    clientProtocol.transform(connectionFlow)
  }

  val handler = Sink.onComplete {
    case Success(_) =>
      println("Shutting down client")
      system.shutdown()
    case Failure(e) =>
      println("Failure: " + e.getMessage)
      e.printStackTrace()
      system.shutdown()
  }
}
