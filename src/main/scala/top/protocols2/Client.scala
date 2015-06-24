package top.protocols2

import akka.actor.ActorSystem
import akka.stream.ActorFlowMaterializer
import akka.stream.scaladsl._
import akka.util.ByteString

import scala.util.{Failure, Success}

object Client extends App {

  /**
   * Use parameters `host port` to connect to server at host:port.
   */
  implicit val system = ActorSystem("Client")
  val Array(host, port) = args
  client(host, port.toInt)

  def client(address: String, port: Int)(implicit system: ActorSystem): Unit = {
    implicit val materializer = ActorFlowMaterializer()

    val connectionFlow = Tcp().outgoingConnection(address, port)

    val transformations = connectionFlow.via(Image.inbound)

    val handler = Sink.onComplete {
      case Success(_) =>
        println("Shutting down client")
        system.shutdown()
      case Failure(e) =>
        println("Failure: " + e.getMessage)
        e.printStackTrace()
        system.shutdown()
    }

    val modifiedImages = Source.single(ByteString(77))
      .log("sent")
      .via(transformations)
      .log("RECEIVED")

    modifiedImages.runWith(handler)
  }
}
