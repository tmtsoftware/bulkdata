package tmt.server

import tmt.app.{ActorConfigs, AppSettings}
import tmt.io.ImageReadService
import tmt.marshalling.BinaryMarshallers
import tmt.shared.models.Role
import tmt.transformations.{ImageTransformations, MetricsTransformations}

class RouteInstances(
  routeFactory: RouteFactory,
  imageTransformations: ImageTransformations,
  metricsTransformations: MetricsTransformations,
  imageReadService: ImageReadService,
  appSettings: AppSettings
) extends BinaryMarshallers {

  val serverName = appSettings.binding.name

  def find(role: Role) = role match {
    case Role.Source    => routeFactory.images(serverName, imageReadService.sendImages)
    case Role.Rotator   => routeFactory.images(serverName, imageTransformations.rotatedImages)

    case Role.Copier    => routeFactory.generic(serverName, imageTransformations.copiedImages)
    case Role.Filter    => routeFactory.generic(serverName, imageTransformations.filteredImages)

    case Role.Metric    => routeFactory.generic(serverName, imageTransformations.imageMetrics)
    case Role.Frequency => routeFactory.generic(serverName, metricsTransformations.perSecMetrics)
  }
}
