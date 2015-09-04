package tmt.wavefront

import tmt.common.CommonMarshallers
import tmt.media.server.{ImageWriteService, ImageReadService}

class RouteInstances(
  routeFactory: RouteFactory,
  imageTransformations: ImageTransformations,
  imageSourceService: ImageSourceService,
  imageReadService: ImageReadService,
  imageWriteService: ImageWriteService
) extends CommonMarshallers {

  private val upstreamImages = imageSourceService.images

  val images = routeFactory.make(RouteFactory.ImageMetricsRoute, imageReadService.sendImages)
  val filteredImages = routeFactory.make(RouteFactory.FilteredImagesRoute, upstreamImages.via(imageTransformations.filteredImages))
  val copiedImages = routeFactory.make(RouteFactory.CopiedImagesRoute, upstreamImages.via(imageTransformations.copiedImages(imageWriteService.copyImage)))

  val imageMetrics = routeFactory.make(RouteFactory.ImageMetricsRoute, upstreamImages.via(imageTransformations.imageMetrics))
  val cumulativeMetrics = routeFactory.make(RouteFactory.CumulativeMetricsRoute, upstreamImages.via(imageTransformations.cumulativeMetrics))
  val perSecMetrics = routeFactory.make(RouteFactory.PerSecMetricsRoute, upstreamImages.via(imageTransformations.perSecMetrics))
}
