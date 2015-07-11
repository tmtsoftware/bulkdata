package tmt.dsl

import java.net.InetSocketAddress

import akka.http.scaladsl.model.{Uri, HttpResponse, MessageEntity}
import akka.stream.scaladsl.{Merge, Broadcast, FlowGraph, Flow}
import tmt.common.ActorConfigs

class OneToManyTransfer(source: InetSocketAddress, destinations: Seq[InetSocketAddress])(implicit actorConfigs: ActorConfigs) {

  val producingClient = new ProducingClient(source)
  val consumingClients = destinations.map(new ConsumingClient(_))

  val flow = Flow() { implicit b =>
    import FlowGraph.Implicits._

    val pipe = b.add(producingClient.flow)
    val broadcast = b.add(Broadcast[(MessageEntity, Uri)](destinations.size))
    val merge = b.add(Merge[HttpResponse](destinations.size))

    pipe.outlet ~> broadcast.in
    
    consumingClients.zipWithIndex.foreach { case (consumingClient, i) =>
      broadcast.out(i) ~> consumingClient.flow ~> merge.in(i)
    }

    (pipe.inlet, merge.out)
  }

}
