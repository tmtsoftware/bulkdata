package tmt.shared.models

import boopickle.Default._

case class ImageMetadata(name: String, size: Int, createdAt: Long) {
  def latency = (System.currentTimeMillis() - createdAt).toDouble
}

case class ImageMetric(size: Int, latency: Double)

object ImageMetric {
  def from(imageInfo: ImageMetadata) = ImageMetric(imageInfo.size, imageInfo.latency)
}

object ImageMetadata {
  implicit val pickler = generatePickler[ImageMetadata]
}

case class PerSecMetric(size: Int, count: Int, latency: Double)

object PerSecMetric {
  def from(imageMetrics: Seq[ImageMetric]) = PerSecMetric(
    imageMetrics.map(_.size).sum,
    imageMetrics.length,
    imageMetrics.map(_.latency).sum / imageMetrics.length
  )

  implicit val pickler = generatePickler[PerSecMetric]
}
