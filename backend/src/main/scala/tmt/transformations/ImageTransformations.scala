package tmt.transformations

import akka.http.scaladsl.model.DateTime
import akka.stream.scaladsl.Source
import tmt.actors.SubscriptionService
import tmt.app.{AppSettings, ActorConfigs}
import tmt.io.WavefrontWriteService
import tmt.shared.models.{Image, ImageMetric}

class ImageTransformations(
  wavefrontWriteService: WavefrontWriteService,
  actorConfigs: ActorConfigs,
  appSettings: AppSettings,
  imageSubscriber: SubscriptionService[Image], 
  imageRotationUtility: ImageProcessor) {

  import actorConfigs._

  lazy val images: Source[Image, Unit] = imageSubscriber.source

  lazy val filteredImages = images.filter(_.name.contains("9"))

  lazy val copiedImages = images.mapAsync(1) { image =>
    wavefrontWriteService.copyImage(image).map(_ => image)
  }

  lazy val imageMetrics = images.map(image => ImageMetric(image.name, image.size, DateTime.now.clicks))

  lazy val rotatedImages = images.mapAsync(appSettings.imageProcessingParallelism)(imageRotationUtility.rotate)
}
