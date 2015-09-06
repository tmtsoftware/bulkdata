package tmt.common
import boopickle.Default._

case class ImageMetric(size: Int, timestamp: Long)

object ImageMetric {
  implicit val pickler = generatePickler[ImageMetric]
}

case class CumulativeMetric(size: Int, count: Int) {
  def +(imageMetric: ImageMetric) = CumulativeMetric(size + imageMetric.size, count + 1)
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
