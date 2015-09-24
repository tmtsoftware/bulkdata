package tmt.api

import akka.actor.{Actor, ActorRef, Props}
import akka.cluster.client.ClusterClientReceptionist
import akka.stream.scaladsl.Sink
import tmt.app.{ActorConfigs, AppSettings}
import tmt.common.models.Messages
import tmt.library.Connector

import scala.concurrent.duration.FiniteDuration

class TickerService(appSettings: AppSettings, actorConfigs: ActorConfigs) {
  import actorConfigs._

  val (actorRef, source) = Connector.coupling[Ticker.Tick](Sink.publisher)
  val ticker = system.actorOf(Ticker.props(appSettings.bindingName, appSettings.imageReadThrottle, actorRef))
  ClusterClientReceptionist(system).registerSubscriber("throttle", ticker)

  def ticks = source
}

class Ticker(serverName: String, initialDelay: FiniteDuration, actorRef: ActorRef) extends Actor {
  import context.dispatcher

  val scheduler = context.system.scheduler

  def startNewSchedule(delay: FiniteDuration) = scheduler.schedule(delay, delay, actorRef, Ticker.Tick)

  var currentSchedule = startNewSchedule(initialDelay)

  def receive = {
    case Messages.UpdateDelay(`serverName`, newDelay) =>
      println("**** new delay is: ", newDelay)
      currentSchedule.cancel()
      currentSchedule = startNewSchedule(newDelay)
  }
}

object Ticker {
  trait Tick
  case object Tick extends Tick

  def props(serverName: String, initialDelay: FiniteDuration, actorRef: ActorRef) = Props(new Ticker(serverName, initialDelay, actorRef))
}
