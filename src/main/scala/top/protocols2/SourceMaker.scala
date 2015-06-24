package top.protocols2

import akka.actor.{ActorRef, ActorSystem, PoisonPill}
import akka.stream.scaladsl._
import akka.stream.{ActorFlowMaterializer, FlowMaterializer, OverflowStrategy}

import scala.util.{Failure, Success}

object SourceMaker extends App {

  implicit val system = ActorSystem("Server")
  implicit val materializer = ActorFlowMaterializer()
  import system.dispatcher

  val (source, actorRef) = SourceFactory.actorRef[Int](4, OverflowStrategy.dropHead)

  val result = source.runForeach(println)

  msg(actorRef)

  result.onComplete {
    case Success(b) =>
      println("stream finished")
      system.shutdown()
    case Failure(e) =>
      println(s"error: ${e.getMessage}")
      system.shutdown()
  }


  def msg(actorRef: ActorRef) = {
    (1 to 50).foreach { n =>
      actorRef ! n
      Thread.sleep(0, 1)
    }

    actorRef ! PoisonPill
  }
}

object SourceFactory {
  def actorRef[T](bufferSize: Int, overflowStrategy: OverflowStrategy)(implicit materializer: FlowMaterializer) = {
    val (actorRef, publisher) = Source.actorRef[T](bufferSize, overflowStrategy).toMat(Sink.publisher)(Keep.both).run()
    (Source(publisher), actorRef)
  }
}
