package tmt.media

import akka.http.scaladsl.model.{HttpResponse, MessageEntity, Uri}
import akka.stream.scaladsl.{Broadcast, Flow, FlowGraph, Merge}
import tmt.common.ActorConfigs

class OneToManyTransfer(producingClient: ProducingClient, consumingClients: Seq[ConsumingClient], actorConfigs: ActorConfigs) {

  val flow = Flow() { implicit b =>
    import FlowGraph.Implicits._

    val pipe = b.add(producingClient.flow)
    val broadcast = b.add(Broadcast[(MessageEntity, Uri)](consumingClients.size))
    val merge = b.add(Merge[HttpResponse](consumingClients.size))

    pipe.outlet ~> broadcast.in
    
    consumingClients.zipWithIndex.foreach { case (consumingClient, i) =>
      broadcast.out(i) ~> consumingClient.flow ~> merge.in(i)
    }

    (pipe.inlet, merge.out)
  }

}
