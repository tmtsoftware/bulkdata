package tmt.shared.models

import boopickle.Default._

case class ImageMetadata(name: String, size: Int, createdAt: Long) {
  def latency = System.currentTimeMillis() - createdAt
}

case class ImageMetric(size: Int, latency: Long)

object ImageMetric {
  def from(imageInfo: ImageMetadata) = ImageMetric(imageInfo.size, imageInfo.latency)
}

object ImageMetadata {
  implicit val pickler = generatePickler[ImageMetadata]
}

case class PerSecMetric(size: Int, count: Int, latency: Double)

object PerSecMetric {
  def from(imageMetrics: Seq[ImageMetric]) = {
    val totalLatency = imageMetrics.map(_.latency).sum
    PerSecMetric(
      imageMetrics.map(_.size).sum,
      imageMetrics.length,
      totalLatency.toDouble / imageMetrics.length
    )
  }

  implicit val pickler = generatePickler[PerSecMetric]
}
