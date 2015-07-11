package tmt.media.client

import tmt.common.ActorConfigs

class OneToOneTransfer(producingClient: ProducingClient, consumingClient: ConsumingClient, actorConfigs: ActorConfigs) {
  val transferFlow = producingClient.flow.via(consumingClient.flow)
}
