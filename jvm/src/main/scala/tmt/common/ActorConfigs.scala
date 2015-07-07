package tmt.common

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

class ActorConfigs(name: String) {
  implicit val system = ActorSystem(name)
  implicit val mat = ActorMaterializer()
  implicit val ec = system.dispatcher
}
