package tmt.wavefront

import tmt.common.ImageMarshallers
import tmt.media.client.ProducingClient

class ImageGateway {
  def getImages(client: ProducingClient) = client
    .request(RouteFactory.ImageRoute)
    .map(ImageMarshallers.imageReads.reads)
}
