package tmt.api

import akka.actor.{Actor, ActorRef, Props}
import tmt.app.{ActorConfigs, AppSettings}
import tmt.common.models.Messages

import scala.concurrent.duration.FiniteDuration

class TickerService(
  appSettings: AppSettings, actorConfigs: ActorConfigs
) extends ClusterReceptionistService[Ticker.Tick]("throttle", actorConfigs) {
  import actorConfigs._
  def wrap(ref: ActorRef) = system.actorOf(Ticker.props(appSettings.bindingName, appSettings.imageReadThrottle, ref))
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
