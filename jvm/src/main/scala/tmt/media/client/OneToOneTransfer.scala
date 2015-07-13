package tmt.media.client

import tmt.common.ActorConfigs

class OneToOneTransfer(producingClient: ProducingClient, consumingClient: ConsumingClient, actorConfigs: ActorConfigs) {
  val flow = producingClient.flow.via(consumingClient.flow)
}
