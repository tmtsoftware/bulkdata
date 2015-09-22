package tmt.app

import akka.actor.ActorSystem
import akka.stream.Materializer

import scala.concurrent.ExecutionContext

class ActorConfigs(_system: ActorSystem, _mat: Materializer, _ec: ExecutionContext) {
  implicit val system = _system
  implicit val mat    = _mat
  implicit val ec     = _ec
}
