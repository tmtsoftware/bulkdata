package tmt.wavefront

import tmt.common.models.{CumulativeMetric, ImageMetric, PerSecMetric}

import scala.concurrent.duration.DurationInt

class MetricsTransformations(itemSourceService: ItemSourceService) {
  val imageMetrics = itemSourceService.getItems[ImageMetric](Routes.Metrics)
  val cumulativeMetrics = imageMetrics.scan(CumulativeMetric("", 0, 0, 0, 0))(_ + _)
  val perSecMetrics = imageMetrics.groupedWithin(10000, 1.second).map(PerSecMetric.fromImageMetrics)
}
