package tmt.wavefront

import tmt.common.{ImageMarshallers, JsonMarshallers}
import tmt.media.server.ImageReadService

class RouteInstances(
  routeFactory: RouteFactory,
  imageTransformations: ImageTransformations,
  imageReadService: ImageReadService
) {

  object Binary extends ImageMarshallers {
    val images = routeFactory.make(RouteFactory.ImageMetricsRoute, imageReadService.sendImages)
    val filteredImages = routeFactory.make(RouteFactory.FilteredImagesRoute, imageTransformations.filteredImages)
    val copiedImages = routeFactory.make(RouteFactory.CopiedImagesRoute, imageTransformations.copiedImages)
  }

  object Json extends JsonMarshallers {
    val imageMetrics = routeFactory.make(RouteFactory.ImageMetricsRoute, imageTransformations.imageMetrics)
    val cumulativeMetrics = routeFactory.make(RouteFactory.CumulativeMetricsRoute, imageTransformations.cumulativeMetrics)
    val perSecMetrics = routeFactory.make(RouteFactory.PerSecMetricsRoute, imageTransformations.perSecMetrics)
  }
}
