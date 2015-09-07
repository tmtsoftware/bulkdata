package tmt.common.models

import boopickle.Default._

case class ImageMetric(name: String, size: Int, timestamp: Long)

object ImageMetric {
  implicit val pickler = generatePickler[ImageMetric]
}

case class CumulativeMetric(name: String, cumulativeSize: Long, cumulativeCount: Long, currentSize: Long, timestamp: Long) {
  def +(imageMetric: ImageMetric) = CumulativeMetric(
    imageMetric.name,
    cumulativeSize + imageMetric.size,
    cumulativeCount + 1,
    imageMetric.size,
    imageMetric.timestamp
  )
}

object CumulativeMetric {
  implicit val pickler = generatePickler[CumulativeMetric]
}

case class PerSecMetric(size: Int, count: Int)

object PerSecMetric {
  def fromImageMetrics(imageMetrics: Seq[ImageMetric]) = PerSecMetric(
    imageMetrics.map(_.size).sum,
    imageMetrics.length
  )

  implicit val pickler = generatePickler[PerSecMetric]
}
