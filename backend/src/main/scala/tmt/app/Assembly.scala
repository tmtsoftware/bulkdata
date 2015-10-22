package tmt.app

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import tmt.actors.{SubscriptionService, TickerService}
import tmt.clients._
import tmt.io._
import tmt.server._
import tmt.shared.models.{Image, ImageMetric}
import tmt.transformations.{ImageProcessor, ImageTransformations, MetricsTransformations}

class Assembly(role: String, serverName: String, env: String, seedName: Option[String]) {
  import com.softwaremill.macwire._

  lazy val configLoader = wire[ConfigLoader]

  lazy val system = ActorSystem("ClusterSystem", configLoader.load(role, serverName, env, seedName))
  lazy val ec     = system.dispatcher
  lazy val mat    = ActorMaterializer()(system)

  lazy val actorConfigs: ActorConfigs = wire[ActorConfigs]

  lazy val appSettings: AppSettings = wire[AppSettings]

  lazy val nodeInfoPublisher = wire[NodeInfoPublisher]

  lazy val producer = wire[Producer]

  lazy val wavefrontReadService: WavefrontReadService = wire[WavefrontReadService]
  lazy val wavefrontWriteService                      = wire[WavefrontWriteService]
  lazy val movieReadService                           = wire[MovieReadService]
  lazy val movieWriteService                          = wire[MovieWriteService]

  lazy val producingClientFactory = wire[ProducingClientFactory]
  lazy val consumingClientFactory = wire[ConsumingClientFactory]

  lazy val oneToOneTransferFactory  = wire[OneToOneTransferFactory]
  lazy val oneToManyTransferFactory = wire[OneToManyTransferFactory]

  lazy val publisher              = wire[Publisher]
  lazy val imageSubscriber        = wire[SubscriptionService[Image]]
  lazy val metricSubscriber       = wire[SubscriptionService[ImageMetric]]

  lazy val imageTransformations   = wire[ImageTransformations]
  lazy val metricsTransformations = wire[MetricsTransformations]
  lazy val imageProcessor         = wire[ImageProcessor]

  lazy val routeFactory           = wire[RouteFactory]
  lazy val routeInstances         = wire[RouteInstances]
  lazy val serverFactory          = wire[ServerFactory]

  lazy val tickerService = wire[TickerService]
}
