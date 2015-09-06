package tmt.wavefront

import tmt.marshalling.BinaryMarshallers
import tmt.media.server.ImageReadService

class RouteInstances(
  routeFactory: RouteFactory,
  imageTransformations: ImageTransformations,
  imageReadService: ImageReadService
) extends BinaryMarshallers {

  def find(role: String) = role match {
    case Roles.ImageSource => routeFactory.make(Routes.Images, imageReadService.sendImages)

    case Roles.ImageCopy   => routeFactory.make(Routes.Images, imageTransformations.copiedImages)
    case Roles.ImageFilter => routeFactory.make(Routes.Images, imageTransformations.filteredImages)

    case Roles.MetricsCumulative => routeFactory.make(Routes.Metrics, imageTransformations.cumulativeMetrics)
    case Roles.MetricsPerSec     => routeFactory.make(Routes.Metrics, imageTransformations.perSecMetrics)
  }
}
