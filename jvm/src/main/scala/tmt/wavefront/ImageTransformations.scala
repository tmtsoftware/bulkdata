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

  val images = imageSourceService.getItems[Image](Routes.Images)

  val filteredImages = images.filter(_.name.contains("9"))

  val copiedImages = images.mapAsync(1) { image =>
    imageWriteService.copyImage(image).map(_ => image)
  }

  val imageMetrics = images.map(image => ImageMetric(image.name, image.size, DateTime.now.clicks))
}
