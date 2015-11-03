package tmt.transformations

import tmt.actors.SubscriptionService
import tmt.shared.models.{ImageMetric, ImageMetadata, PerSecMetric}

import scala.concurrent.duration.DurationInt

class MetricsTransformations(imageInfoSubscriber: SubscriptionService[ImageMetadata]) {
  lazy val imageMetrics = imageInfoSubscriber.source.map(ImageMetric.from)
  lazy val perSecMetrics = imageMetrics.groupedWithin(10000, 1.second).map(PerSecMetric.from)
}
