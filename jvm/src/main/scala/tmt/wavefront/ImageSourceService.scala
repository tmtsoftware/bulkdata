package tmt.wavefront

import tmt.common.{Image, AppSettings}
import tmt.library.SourceExtensions
import tmt.media.client.ProducingClientFactory

class ImageSourceService(appSettings: AppSettings, producingClientFactory: ProducingClientFactory) {
  val images = {
    val producingClients = appSettings.upstreamServers.map(producingClientFactory.make)
    val imageSources = producingClients.map(_.request[Image](RouteFactory.ImageRoute))
    SourceExtensions.merge(imageSources)
  }
}
