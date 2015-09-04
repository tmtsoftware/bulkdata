package tmt.wavefront

import play.api.libs.json.Json

case class ImageMetric(size: Int, timestamp: Long)

object ImageMetric {
  implicit val format = Json.format[ImageMetric]
}

case class CumulativeMetric(size: Int, count: Int) {
  def +(imageMetric: ImageMetric) = CumulativeMetric(size + imageMetric.size, count + 1)
}

object CumulativeMetric {
  implicit val format = Json.format[CumulativeMetric]
}

case class PerSecMetric(size: Int, count: Int)

object PerSecMetric {
  def fromImageMetrics(imageMetrics: Seq[ImageMetric]) = PerSecMetric(
    imageMetrics.map(_.size).sum,
    imageMetrics.length
  )

  implicit val format = Json.format[PerSecMetric]
}
