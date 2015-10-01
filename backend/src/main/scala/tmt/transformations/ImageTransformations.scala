package tmt.transformations

import akka.http.scaladsl.model.DateTime
import akka.stream.scaladsl.Source
import tmt.actors.SubscriptionService
import tmt.app.ActorConfigs
import tmt.io.ImageWriteService
import tmt.shared.models.{ImageMetric, Image}

class ImageTransformations(
  imageWriteService: ImageWriteService,
  actorConfigs: ActorConfigs,
  imageSubscriber: SubscriptionService[Image]) {

  import actorConfigs._

  lazy val images: Source[Image, Unit] = imageSubscriber.source

  lazy val filteredImages = images.filter(_.name.contains("9"))

  lazy val copiedImages = images.mapAsync(1) { image =>
    imageWriteService.copyImage(image).map(_ => image)
  }

  lazy val imageMetrics = images.map(image => ImageMetric(image.name, image.size, DateTime.now.clicks))
}
