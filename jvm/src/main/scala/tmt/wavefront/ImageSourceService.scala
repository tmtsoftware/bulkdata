package tmt.wavefront

import tmt.common.AppSettings
import tmt.library.SourceExtensions
import tmt.media.client.ProducingClientFactory

class ImageSourceService(appSettings: AppSettings, producingClientFactory: ProducingClientFactory, imageGateway: ImageGateway) {
  val images = {
    val producingClients = appSettings.upstreamServers.map(producingClientFactory.make)
    val imageSources = producingClients.map(imageGateway.getImages)
    SourceExtensions.merge(imageSources)
  }
}
