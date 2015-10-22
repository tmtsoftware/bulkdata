package tmt.server

import tmt.app.{ActorConfigs, AppSettings}
import tmt.io.WavefrontReadService
import tmt.library.SourceExtensions.RichSource
import tmt.marshalling.BinaryMarshallers
import tmt.shared.models.Role
import tmt.transformations.{ImageTransformations, MetricsTransformations}

class RouteInstances(
  routeFactory: RouteFactory,
  imageTransformations: ImageTransformations,
  metricsTransformations: MetricsTransformations,
  actorConfigs: ActorConfigs,
  wavefrontReadService: WavefrontReadService,
  appSettings: AppSettings
) extends BinaryMarshallers {

  import actorConfigs._

  val serverName = appSettings.binding.name

  def find(role: Role) = role match {
    case Role.Wavefront => routeFactory.images(serverName, wavefrontReadService.sendImages.hotMulticast)
    case Role.Rotator   => routeFactory.images(serverName, imageTransformations.rotatedImages)

    case Role.Copier => routeFactory.generic(serverName, imageTransformations.copiedImages)
    case Role.Filter => routeFactory.generic(serverName, imageTransformations.filteredImages)

    case Role.Metric    => routeFactory.generic(serverName, imageTransformations.imageMetrics)
    case Role.Frequency => routeFactory.generic(serverName, metricsTransformations.perSecMetrics)
  }
}
