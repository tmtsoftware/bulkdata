package tmt.wavefront

import akka.http.scaladsl.model.DateTime
import akka.stream.scaladsl.Flow
import play.api.libs.json.Json
import tmt.common.Image
import tmt.library.FlowExtensions.RichFlow

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

class ImageTransformations {
  val images = Flow[Image]

  val filteredImages = images.zipWithIndex.collect {
    case (image, index) if index % 3 == 0 => image
  }

  def copiedImages(copier: Image => Future[Unit]) = images.map { image =>
    copier(image)
    image
  }

  val imageMetrics = images.map(image => ImageMetric(image.size, DateTime.now.clicks))
  val cumulativeMetrics = imageMetrics.scan(CumulativeMetric(0, 0))(_ + _)
  val perSecMetrics = imageMetrics.groupedWithin(10000, 1.second).map(PerSecMetric.fromImageMetrics)
}

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
