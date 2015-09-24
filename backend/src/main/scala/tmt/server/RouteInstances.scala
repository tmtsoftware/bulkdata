package tmt.server

import akka.util.ByteString
import tmt.app.ActorConfigs
import tmt.io.ImageReadService
import tmt.library.SourceExtensions.RichSource
import tmt.marshalling.BinaryMarshallers
import tmt.transformations.{ImageTransformations, MetricsTransformations}

class RouteInstances(
  routeFactory: RouteFactory,
  imageTransformations: ImageTransformations,
  metricsTransformations: MetricsTransformations,
  imageReadService: ImageReadService,
  actorConfigs: ActorConfigs
) extends BinaryMarshallers {

  import actorConfigs._

  def find(role: Role) = role match {
    case Role.Source      =>
      val images = imageReadService.sendImages.hotMulticast
      routeFactory.make(role, images, images.map(x => ByteString(x.bytes)))
    case Role.Copier      => routeFactory.make(role, imageTransformations.copiedImages)
    case Role.Filter      => routeFactory.make(role, imageTransformations.filteredImages)
    case Role.Metric      => routeFactory.make(role, imageTransformations.imageMetrics)
    case Role.Accumulator => routeFactory.make(Role.Accumulator, metricsTransformations.cumulativeMetrics)
    case Role.Frequency   => routeFactory.make(Role.Frequency, metricsTransformations.perSecMetrics)
  }
}
