package tmt.wavefront

import akka.http.scaladsl.model.DateTime
import tmt.common.models.{PerSecMetric, CumulativeMetric, ImageMetric}
import tmt.library.SourceExtensions.RichSource
import tmt.media.server.ImageWriteService

import scala.concurrent.duration.DurationInt

class ImageTransformations(imageSourceService: ImageSourceService, imageWriteService: ImageWriteService) {
  val images = imageSourceService.images

  val filteredImages = images.zipWithIndex.collect {
    case (image, index) if index % 3 == 0 => image
  }

  val copiedImages = images.map { image =>
    imageWriteService.copyImage(image)
    image
  }

  val imageMetrics = images.map(image => ImageMetric(image.size, DateTime.now.clicks))
  val cumulativeMetrics = imageMetrics.scan(CumulativeMetric(0, 0, 0, 0))(_ + _)
  val perSecMetrics = imageMetrics.groupedWithin(10000, 1.second).map(PerSecMetric.fromImageMetrics)
}
