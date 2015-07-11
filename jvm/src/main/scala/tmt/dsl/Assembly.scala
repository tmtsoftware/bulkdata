package tmt.dsl

import java.net.InetSocketAddress

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import tmt.common.{AppSettings, ActorConfigs, Server}
import tmt.http.Handler
import tmt.library.RequestHandlerExtensions.RichRequestHandler

class Assembly(name: String) {
  import com.softwaremill.macwire._
  lazy val actorConfigs: ActorConfigs = wire[ActorConfigs]

  implicit lazy val system = ActorSystem(name)
  implicit lazy val ec     = system.dispatcher
  implicit lazy val mat    = ActorMaterializer()

  lazy val appSettings = wire[AppSettings]

  lazy val imageService           = wire[MediaService]
  lazy val mediaRoute: MediaRoute = wire[MediaRoute]
  lazy val handler   : Handler    = wire[Handler]
  lazy val boxService = wire[BoxService]
  lazy val boxRoute = wire[BoxRoute]

  def mediaServer(address: InetSocketAddress) = new Server(address, mediaRoute.route, actorConfigs)
  def httpServer(address: InetSocketAddress) = new Server(address, handler.requestHandler.toFlow, actorConfigs)

  def simpleTransfer(source: InetSocketAddress, destination: InetSocketAddress) =
    new SimpleTransfer(source, destination, actorConfigs)

  def producingClient(address: InetSocketAddress): ProducingClient = wire[ProducingClient]
  def consumingClient(address: InetSocketAddress): ConsumingClient = wire[ConsumingClient]

  def oneToOneTransfer(source: InetSocketAddress, destination: InetSocketAddress) =
    new OneToOneTransfer(producingClient(source), consumingClient(destination), actorConfigs)

  def oneToManyTransfer(source: InetSocketAddress, destinations: Seq[InetSocketAddress]) =
    new OneToManyTransfer(producingClient(source), destinations.map(consumingClient), actorConfigs)
}
