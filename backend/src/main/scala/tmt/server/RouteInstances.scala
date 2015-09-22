package tmt.server

import akka.http.scaladsl.server.Directives._
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
    case Role.ImageSource     =>
      val images = imageReadService.sendImages.hotMulticast
      routeFactory.make(role, images, images.map(x => ByteString(x.bytes)))
    case Role.ImageCopy       => routeFactory.make(role, imageTransformations.copiedImages)
    case Role.ImageFilter     => routeFactory.make(role, imageTransformations.filteredImages)
    case Role.MetricsPerImage => routeFactory.make(role, imageTransformations.imageMetrics)
    case Role.MetricsAgg      =>
      routeFactory.make(Role.MetricsCumulative, metricsTransformations.cumulativeMetrics) ~
        routeFactory.make(Role.MetricsPerSec, metricsTransformations.perSecMetrics)
  }
}
