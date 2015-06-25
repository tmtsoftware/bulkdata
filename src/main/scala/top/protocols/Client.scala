package top.protocols

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Tcp}

import scala.util.{Failure, Success}

class Client(address: String, port: Int, clientProtocol: ClientProtocol)(implicit system: ActorSystem) {
  implicit val materializer = ActorMaterializer()

  def run(): Unit = clientProtocol.transform(connectionFlow).runWith(handler)

  val connectionFlow = Tcp().outgoingConnection(address, port)

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
