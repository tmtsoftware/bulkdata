package tmt.media

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import tmt.common._
import tmt.media.client._
import tmt.media.server._
import tmt.wavefront._

class MediaAssembly(name: String, env: String = "dev") {
  import com.softwaremill.macwire._

  lazy val configLoader = wire[ConfigLoader]

  lazy val system = ActorSystem(name, configLoader.load(name, env))
  lazy val ec     = system.dispatcher
  lazy val mat    = ActorMaterializer()(system)

  lazy val actorConfigs: ActorConfigs = wire[ActorConfigs]

  lazy val appSettings: AppSettings = wire[AppSettings]

  lazy val producer = wire[Producer]

  lazy val imageReadService       = wire[ImageReadService]
  lazy val movieReadService       = wire[MovieReadService]
  lazy val imageWriteService      = wire[ImageWriteService]
  lazy val movieWriteService      = wire[MovieWriteService]
  lazy val mediaRoute: MediaRoute = wire[MediaRoute]

  lazy val producingClientFactory = wire[ProducingClientFactory]
  lazy val consumingClientFactory = wire[ConsumingClientFactory]

  lazy val oneToOneTransferFactory  = wire[OneToOneTransferFactory]
  lazy val oneToManyTransferFactory = wire[OneToManyTransferFactory]

  lazy val simpleTransferFactory = wire[SimpleTransferFactory]
  lazy val mediaServerFactory    = wire[MediaServerFactory]

  lazy val imageSourceService     = wire[ItemSourceService]
  lazy val imageTransformations   = wire[ImageTransformations]
  lazy val metricsTransformations = wire[MetricsTransformations]
  lazy val routeFactory           = wire[RouteFactory]
  lazy val routeInstances         = wire[RouteInstances]
  lazy val serverFactory          = wire[ServerFactory]


  lazy val binding = appSettings.topology.binding
}
