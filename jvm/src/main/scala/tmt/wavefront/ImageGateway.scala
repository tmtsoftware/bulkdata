package tmt.wavefront

import tmt.common.ImageConverter
import tmt.media.client.ProducingClient

class ImageGateway {
  def getImages(client: ProducingClient) = client
    .request(RouteFactory.ImageRoute)
    .map(ImageConverter.fromByteString)
}
