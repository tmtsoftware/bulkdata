package tmt.media.client

import java.net.InetSocketAddress

class OneToOneTransfer(producingClient: ProducingClient, consumingClient: ConsumingClient) {
  val flow = producingClient.flow.via(consumingClient.flow)
}

class OneToOneTransferFactory(producingClientFactory: ProducingClientFactory, consumingClientFactory: ConsumingClientFactory) {
  def make(source: InetSocketAddress, destination: InetSocketAddress) = new OneToOneTransfer(
    producingClientFactory.make(source),
    consumingClientFactory.make(source)
  )
}
