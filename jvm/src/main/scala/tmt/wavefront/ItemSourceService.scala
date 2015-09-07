package tmt.wavefront

import tmt.common.AppSettings
import tmt.common.models.Image
import tmt.library.SourceExtensions
import tmt.marshalling.BFormat
import tmt.media.client.ProducingClientFactory

class ItemSourceService(appSettings: AppSettings, producingClientFactory: ProducingClientFactory) {
  def getItems[T: BFormat](routeName: String) = {
    val producingClients = appSettings.topology.sources.map(producingClientFactory.make)
    val imageSources = producingClients.map(_.request[T]("/" + routeName))
    SourceExtensions.merge(imageSources)
  }
}
