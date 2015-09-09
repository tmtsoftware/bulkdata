package tmt.wavefront

import tmt.marshalling.BinaryMarshallers
import tmt.media.server.{MediaRoute, ImageReadService}
import akka.http.scaladsl.server.Directives._

class RouteInstances(
  routeFactory: RouteFactory,
  imageTransformations: ImageTransformations,
  metricsTransformations: MetricsTransformations,
  imageReadService: ImageReadService,
  mediaRoute: MediaRoute
) extends BinaryMarshallers {

  def find(role: Role) = role match {
    case Role.ImageSource => routeFactory.make(
      Routes.Images, imageReadService.sendImages.map(x => {Thread.sleep(1); println(s"11111: serving image ${x.name}"); x})
    )

    case Role.ImageCopy   => routeFactory.make(
      Routes.Images, imageTransformations.copiedImages.map(x => {println(s"22222: serving image ${x.name}"); x})
    )

    case Role.ImageFilter => routeFactory.make(
      Routes.Images, imageTransformations.filteredImages.map(x => {println(s"33333: serving image ${x.name}"); x})
    )

    case Role.MetricsPerImage => routeFactory.make(
      Routes.Metrics, imageTransformations.imageMetrics.map(x => {println(s"44444: serving metric $x"); x})
    )

    case Role.MetricsAgg =>
      routeFactory.websocket(
        Routes.MetricsCumulative, metricsTransformations.cumulativeMetrics.map(x => {println(s"55555: serving metric $x"); x})
      ) ~ routeFactory.websocket(
        Routes.MetricsPerSec, metricsTransformations.perSecMetrics.map(x => {println(s"66666: serving metric $x"); x})
      ) ~ mediaRoute.route
  }
}
