package tmt.media

import akka.actor.{Props, ActorSystem}
import akka.stream.ActorMaterializer
import tmt.common._
import tmt.common.models.{ImageMetric, Image}
import tmt.media.client._
import tmt.media.server._
import tmt.pubsub.{Subscriber, Publisher}
import tmt.wavefront._

class MediaAssembly(name: String, env: String = "dev") {
  import com.softwaremill.macwire._

  lazy val configLoader = wire[ConfigLoader]

  lazy val system = ActorSystem("ClusterSystem", configLoader.load(name, env))
  lazy val ec     = system.dispatcher
  lazy val mat    = ActorMaterializer()(system)

  lazy val actorConfigs: ActorConfigs = wire[ActorConfigs]

  lazy val appSettings: AppSettings = wire[AppSettings]

  lazy val producer = wire[Producer]

  lazy val imageReadService: ImageReadService = wire[ImageReadService]
  lazy val movieReadService                   = wire[MovieReadService]
  lazy val imageWriteService                  = wire[ImageWriteService]
  lazy val movieWriteService                  = wire[MovieWriteService]
  lazy val mediaRoute      : MediaRoute       = wire[MediaRoute]

  lazy val producingClientFactory = wire[ProducingClientFactory]
  lazy val consumingClientFactory = wire[ConsumingClientFactory]

  lazy val oneToOneTransferFactory  = wire[OneToOneTransferFactory]
  lazy val oneToManyTransferFactory = wire[OneToManyTransferFactory]

  lazy val publisher              = wire[Publisher]
  lazy val imageSubscriber        = wire[Subscriber[Image]]
  lazy val metricSubscriber       = wire[Subscriber[ImageMetric]]
  lazy val imageTransformations   = wire[ImageTransformations]
  lazy val metricsTransformations = wire[MetricsTransformations]
  lazy val routeFactory           = wire[RouteFactory]
  lazy val routeInstances         = wire[RouteInstances]
  lazy val serverFactory          = wire[ServerFactory]

  lazy val binding = appSettings.binding.httpAddress
}
