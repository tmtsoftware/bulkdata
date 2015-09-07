package tmt.wavefront

import tmt.marshalling.BinaryMarshallers
import tmt.media.server.ImageReadService

class RouteInstances(
  routeFactory: RouteFactory,
  imageTransformations: ImageTransformations,
  imageReadService: ImageReadService
) extends BinaryMarshallers {

  def find(role: String) = role match {
    case Roles.ImageSource => routeFactory.make(Routes.Images, imageReadService.sendImages
      .map(x => {Thread.sleep(1); println(s"11111: serving image ${x.name}"); x}))

    case Roles.ImageCopy   => routeFactory.make(Routes.Images, imageTransformations.copiedImages
      .map(x => {println(s"22222: serving image ${x.name}"); x}))

    case Roles.ImageFilter => routeFactory.make(Routes.Images, imageTransformations.filteredImages
      .map(x => {println(s"33333: serving image ${x.name}"); x}))

    case Roles.MetricsCumulative => routeFactory.make(Routes.Metrics, imageTransformations.cumulativeMetrics
      .map(x => {println(s"44444: serving image $x"); x}))

    case Roles.MetricsPerSec     => routeFactory.make(Routes.Metrics, imageTransformations.perSecMetrics
      .map(x => {println(s"55555: serving image $x"); x}))
  }
}
