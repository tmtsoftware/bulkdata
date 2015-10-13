package tmt.shared.models

import boopickle.Default._

case class ImageMetric(name: String, size: Int, timestamp: Long)

object ImageMetric {
  implicit val pickler = generatePickler[ImageMetric]
}

case class PerSecMetric(size: Int, count: Int)

object PerSecMetric {
  def fromImageMetrics(imageMetrics: Seq[ImageMetric]) = PerSecMetric(
    imageMetrics.map(_.size).sum,
    imageMetrics.length
  )

  implicit val pickler = generatePickler[PerSecMetric]
}
