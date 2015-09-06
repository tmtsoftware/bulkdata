package tmt.wavefront

import tmt.common.AppSettings
import tmt.common.models.Image
import tmt.library.SourceExtensions
import tmt.media.client.ProducingClientFactory

class ImageSourceService(appSettings: AppSettings, producingClientFactory: ProducingClientFactory) {
  val images = {
    val producingClients = appSettings.topology.imageSources.map(producingClientFactory.make)
    val imageSources = producingClients.map(_.request[Image]("/" + Routes.Images))
    SourceExtensions.merge(imageSources)
  }
}
