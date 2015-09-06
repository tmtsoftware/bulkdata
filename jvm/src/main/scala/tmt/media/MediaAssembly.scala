package tmt.media

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import tmt.common.{ActorConfigs, AppSettings, ServerFactory}
import tmt.media.client._
import tmt.media.server._
import tmt.wavefront._

class MediaAssembly(name: String) {
  import com.softwaremill.macwire._

  lazy val system = ActorSystem(name)
  lazy val ec     = system.dispatcher
  lazy val mat    = ActorMaterializer()(system)

  lazy val actorConfigs: ActorConfigs = wire[ActorConfigs]

  lazy val appSettings = wire[AppSettings]

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
  lazy val serverFactory         = wire[ServerFactory]

  lazy val copyTransformation     = wire[ImageTransformations]
  lazy val imageSourceService     = wire[ImageSourceService]
}
