package tmt.wavefront

import akka.http.scaladsl.model.DateTime
import akka.stream.scaladsl.Source
import tmt.common.ActorConfigs
import tmt.common.models.{Image, ImageMetric}
import tmt.media.server.ImageWriteService
import tmt.pubsub.{Subscriber, Publisher}

class ImageTransformations(
  imageWriteService: ImageWriteService,
  actorConfigs: ActorConfigs,
  imageSubscriber: Subscriber[Image]) {

  import actorConfigs._

  lazy val images: Source[Image, Unit] = {
    imageSubscriber.subscribe(Role.ImageSource)
    imageSubscriber.source
  }

  lazy val filteredImages = images.filter(_.name.contains("9"))

  lazy val copiedImages = images.mapAsync(1) { image =>
    imageWriteService.copyImage(image).map(_ => image)
  }

  lazy val imageMetrics = images.map(image => ImageMetric(image.name, image.size, DateTime.now.clicks))
}
