package tmt.io

import akka.stream.scaladsl.Sink
import tmt.app.{ActorConfigs, AppSettings}
import tmt.library.{Connector, Ticker}

class Throttler(appSettings: AppSettings, actorConfigs: ActorConfigs) {
  import actorConfigs._

  def coupling = {
    val (actorRef, source) = Connector.coupling[Ticker.Tick](Sink.publisher)
    val props = Ticker.props(appSettings.imageReadThrottle, actorRef)
    val ticker = system.actorOf(props)
    (ticker, source)
  }
}
