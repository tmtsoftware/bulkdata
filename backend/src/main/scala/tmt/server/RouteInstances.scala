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
    case Role.ScienceImageSource => routeFactory.scienceImages

    case Role.Wavefront => routeFactory.wavefront(serverName, wavefrontReadService.sendImages.hotMulticast)
    case Role.Rotator   => routeFactory.wavefront(serverName, imageTransformations.rotatedImages)

    case Role.Copier => routeFactory.generic(serverName, imageTransformations.copiedImages)
    case Role.Filter => routeFactory.generic(serverName, imageTransformations.filteredImages)

    case Role.Metadata    => routeFactory.generic(serverName, imageTransformations.imageMetadata)
    case Role.Metric => routeFactory.generic(serverName, metricsTransformations.perSecMetrics)
  }
}
