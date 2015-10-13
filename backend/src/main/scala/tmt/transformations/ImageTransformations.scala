package tmt.transformations

import akka.http.scaladsl.model.DateTime
import akka.stream.scaladsl.Source
import tmt.actors.SubscriptionService
import tmt.app.ActorConfigs
import tmt.io.ImageWriteService
import tmt.shared.models.{Image, ImageMetric}

class ImageTransformations(
  imageWriteService: ImageWriteService,
  actorConfigs: ActorConfigs,
  imageSubscriber: SubscriptionService[Image], 
  imageRotationUtility: ImageProcessor) {

  import actorConfigs._

  lazy val images: Source[Image, Unit] = imageSubscriber.source

  lazy val filteredImages = images.filter(_.name.contains("9"))

  lazy val copiedImages = images.mapAsync(1) { image =>
    imageWriteService.copyImage(image).map(_ => image)
  }

  lazy val imageMetrics = images.map(image => ImageMetric(image.name, image.size, DateTime.now.clicks))

  lazy val rotatedImages = images.mapAsync(4)(imageRotationUtility.rotate)
}
