package tmt.media.client

import akka.http.scaladsl.model.{HttpResponse, MessageEntity, Uri}
import akka.stream.scaladsl.{Broadcast, Flow, FlowGraph, Merge}

class OneToManyTransfer(producingClient: ProducingClient, consumingClients: Seq[ConsumingClient]) {

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
