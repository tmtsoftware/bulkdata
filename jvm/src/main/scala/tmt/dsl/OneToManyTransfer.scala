package tmt.dsl

import java.net.InetSocketAddress

import akka.http.scaladsl.model.{HttpResponse, MessageEntity}
import akka.stream.scaladsl.{Merge, Broadcast, FlowGraph, Flow}
import tmt.common.ActorConfigs

class OneToManyTransfer(source: InetSocketAddress, destinations: Seq[InetSocketAddress])(implicit actorConfigs: ActorConfigs) {

  val producingClients = new ProducingClient(source)
  val consumingClients = destinations.map(new ConsumingClient(_))

  val transferFlow = Flow() { implicit b =>
    import FlowGraph.Implicits._

    val pipe = b.add(producingClients.producerFlow)
    val broadcast = b.add(Broadcast[(MessageEntity, String)](destinations.size))
    val merge = b.add(Merge[HttpResponse](destinations.size))

    pipe.outlet ~> broadcast.in
    consumingClients.zipWithIndex.foreach { case (tn, i) =>
      broadcast.out(i) ~> tn.consumerFlow ~> merge.in(i)
    }

    (pipe.inlet, merge.out)
  }

}
