package tmt.shared.models

import boopickle.Default._

case class ImageMetadata(name: String, size: Int, createdAt: Long) {
  def latency = {
    val millis = System.nanoTime()
    val diff = millis - createdAt
    println(s"current time is: $millis")
    println(s"CREATED TIME IS: $createdAt")
    println(s"DIFF IS: ${diff/1000000000}")
    diff
  }
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
    val dd = imageMetrics.map(_.latency).sum
    println(s"sum is $dd")
    PerSecMetric(
      imageMetrics.map(_.size).sum,
      imageMetrics.length,
      dd.toDouble / imageMetrics.length
    )
  }

  implicit val pickler = generatePickler[PerSecMetric]
}
