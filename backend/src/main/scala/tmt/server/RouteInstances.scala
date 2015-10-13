package tmt.server

import akka.util.ByteString
import tmt.library.Role
import tmt.app.{AppSettings, ActorConfigs}
import tmt.io.ImageReadService
import tmt.library.SourceExtensions.RichSource
import tmt.marshalling.BinaryMarshallers
import tmt.transformations.{ImageTransformations, MetricsTransformations}

class RouteInstances(
  routeFactory: RouteFactory,
  imageTransformations: ImageTransformations,
  metricsTransformations: MetricsTransformations,
  imageReadService: ImageReadService,
  actorConfigs: ActorConfigs,
  appSettings: AppSettings
) extends BinaryMarshallers {

  import actorConfigs._

  val serverName = appSettings.binding.name

  def find(role: Role) = role match {
    case Role.Source      =>
      val images = imageReadService.sendImages.hotMulticast
      routeFactory.make(serverName, images, images.map(x => ByteString(x.bytes)))
    case Role.Copier      => routeFactory.make(serverName, imageTransformations.copiedImages)
    case Role.Filter      => routeFactory.make(serverName, imageTransformations.filteredImages)
    case Role.Metric      => routeFactory.make(serverName, imageTransformations.imageMetrics)
    case Role.Accumulator => routeFactory.make(serverName, metricsTransformations.cumulativeMetrics)
    case Role.Frequency   => routeFactory.make(serverName, metricsTransformations.perSecMetrics)
    case Role.Rotator     => routeFactory.make(serverName, imageTransformations.rotatedImages)
  }
}
