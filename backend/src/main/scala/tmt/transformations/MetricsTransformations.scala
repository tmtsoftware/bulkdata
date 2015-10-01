package tmt.transformations

import akka.stream.scaladsl.Source
import tmt.actors.SubscriptionService
import tmt.common.models.{CumulativeMetric, ImageMetric, PerSecMetric}

import scala.concurrent.duration.DurationInt

class MetricsTransformations(metricSubscriber: SubscriptionService[ImageMetric]) {
  lazy val imageMetrics: Source[ImageMetric, Unit] = metricSubscriber.source
  lazy val cumulativeMetrics = imageMetrics.scan(CumulativeMetric("", 0, 0, 0, 0))(_ + _)
  lazy val perSecMetrics = imageMetrics.groupedWithin(10000, 1.second).map(PerSecMetric.fromImageMetrics)
}
