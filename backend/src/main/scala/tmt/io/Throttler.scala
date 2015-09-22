package tmt.io

import akka.cluster.client.ClusterClientReceptionist
import akka.stream.scaladsl.Sink
import tmt.app.{ActorConfigs, AppSettings}
import tmt.library.{Connector, Ticker}

class Throttler(appSettings: AppSettings, actorConfigs: ActorConfigs) {
  import actorConfigs._

  def ticks = {
    val (actorRef, source) = Connector.coupling[Ticker.Tick](Sink.publisher)
    val props = Ticker.props(appSettings.imageReadThrottle, actorRef)
    val ticker = system.actorOf(props)
    ClusterClientReceptionist(system).registerSubscriber("image-source-throttle", ticker)
    source
  }
}
