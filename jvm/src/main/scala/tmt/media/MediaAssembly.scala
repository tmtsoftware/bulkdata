package tmt.media

import java.net.InetSocketAddress

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import tmt.common.{AppSettings, ActorConfigs, Server}
import tmt.media.client._
import tmt.media.server._

class MediaAssembly(name: String) {
  import com.softwaremill.macwire._
  lazy val actorConfigs: ActorConfigs = wire[ActorConfigs]

  implicit lazy val system = ActorSystem(name)
  implicit lazy val ec     = system.dispatcher
  implicit lazy val mat    = ActorMaterializer()

  lazy val appSettings = wire[AppSettings]

  lazy val imageReadService       = wire[ImageReadService]
  lazy val movieReadService       = wire[MovieReadService]
  lazy val imageWriteService      = wire[ImageWriteService]
  lazy val movieWriteService      = wire[MovieWriteService]
  lazy val mediaRoute: MediaRoute = wire[MediaRoute]

  def mediaServer(address: InetSocketAddress) = new Server(address, mediaRoute.route, actorConfigs)

  def simpleTransfer(source: InetSocketAddress, destination: InetSocketAddress) =
    new SimpleTransfer(source, destination, actorConfigs)

  def producingClient(address: InetSocketAddress): ProducingClient = wire[ProducingClient]
  def consumingClient(address: InetSocketAddress): ConsumingClient = wire[ConsumingClient]

  def oneToOneTransfer(source: InetSocketAddress, destination: InetSocketAddress) =
    new OneToOneTransfer(producingClient(source), consumingClient(destination))

  def oneToManyTransfer(source: InetSocketAddress, destinations: Seq[InetSocketAddress]) =
    new OneToManyTransfer(producingClient(source), destinations.map(consumingClient))
}
