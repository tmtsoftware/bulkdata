package tmt.boxes.http

import java.net.InetSocketAddress

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import tmt.common.{ActorConfigs, AppSettings, Server}
import tmt.library.RequestHandlerExtensions.RichRequestHandler

class BoxAssembly(name: String) {
  import com.softwaremill.macwire._
  lazy val actorConfigs: ActorConfigs = wire[ActorConfigs]

  implicit lazy val system = ActorSystem(name)
  implicit lazy val ec     = system.dispatcher
  implicit lazy val mat    = ActorMaterializer()

  lazy val appSettings = wire[AppSettings]

  
  lazy val handler   : Handler    = wire[Handler]
  lazy val boxService = wire[BoxService]
  lazy val boxRoute = wire[BoxRoute]

  def boxServer(address: InetSocketAddress) = new Server(address, handler.requestHandler.toFlow, actorConfigs)
}
