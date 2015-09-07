package tmt.wavefront

import akka.http.scaladsl.model.DateTime
import tmt.common.ActorConfigs
import tmt.common.models.{Image, ImageMetric}
import tmt.media.server.ImageWriteService

class ImageTransformations(
  imageSourceService: ItemSourceService,
  imageWriteService: ImageWriteService,
  actorConfigs: ActorConfigs) {

  import actorConfigs._

  lazy val images = imageSourceService.getItems[Image](Routes.Images)

  lazy val filteredImages = images.filter(_.name.contains("9"))

  lazy val copiedImages = images.mapAsync(1) { image =>
    imageWriteService.copyImage(image).map(_ => image)
  }

  lazy val imageMetrics = images.map(image => ImageMetric(image.name, image.size, DateTime.now.clicks))
}
