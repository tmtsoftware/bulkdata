package tmt.media.client

class OneToOneTransfer(producingClient: ProducingClient, consumingClient: ConsumingClient) {
  val flow = producingClient.flow.via(consumingClient.flow)
}
